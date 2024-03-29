# [백엔드] BBANG JOA
## 개요
- 베이킹 레시피를 공유하는 커뮤니티

#### 일정
- 24.01 - present
- 프론트엔드 1명 + 백엔드 1명
- 역할 : 백엔드, CI/CD

## 사용 기술 및 개발 환경
- Cloud : ```AWS EC2(Ubuntu), S3```
- DB : ```MySQL(RDB)```
- Framework : ```Spring Boot, Spring Security, JUnit, Jenkins```
- Language : ```Java```
- Tool : ```IntelliJ, Git, GitHub```

## 내용
#### 구현 기능
##### 1.  회원관리
- Spring Security
- JWT

TokenProvider.java 일부
```
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
	private final JwtProperties jwtProperties;
	private static final String AUTHORITIES_KEY = "auth";

	public String generateToken(Member member, Duration expiredAt) {
		Date now = new Date();
		return makeToken(new Date(now.getTime() + expiredAt.toMillis()), member);
	}

	private String makeToken(Date expiry, Member member) {
		Date now = new Date();

		return Jwts.builder()
				.setSubject(member.getId())
				.claim(AUTHORITIES_KEY, member.getAuthority())
				.setIssuedAt(now)
				.setExpiration(expiry)
				.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
				.compact();
	}
...
	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);
		Collection<? extends GrantedAuthority> authorities =
				Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());
		return new UsernamePasswordAuthenticationToken(new User(claims.getSubject(), "", authorities), token, authorities);
	}

...

}
```

##### 2. 레시피 게시판
RecipeController.java 일부
```
@Slf4j
@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {
	private final RecipeService recipeService;

	@GetMapping("")
	public ResponseEntity<List<RecipeListResponse>> recipeList() {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.getAllRecipes());
	}
	@GetMapping("/{recipeId}")
	public ResponseEntity<RecipeDetailResponse> recipeDetails(@PathVariable("recipeId") Long recipeId) {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.findRecipeByRecipeId(recipeId));
	}

...
}
```

RecipeServiceImp.java 일부
```
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecipeServiceImp implements RecipeService {
	private final RecipeRepository recipeRepository;
	private final ImageService imageService;
	private final RecipeImageRepository recipeImageRepository;
	private final MemberRepository  memberRepository;

	@Override
	public List<RecipeListResponse> getAllRecipes() {
		List<Recipe> recipes = recipeRepository.findAll();

		List<RecipeListResponse> responses = new ArrayList<>();
		for(Recipe recipe : recipes) {
			RecipeListResponse response = RecipeListResponse.of(recipe);
			responses.add(response);
		}
		return responses;
	}
	@Override
	public RecipeDetailResponse findRecipeByRecipeId(Long recipeId) {
		Recipe recipe = recipeRepository.findRecipeById(recipeId);
		if(recipe == null) {
			throw new GlobalException(GlobalErrorCode.NOT_FOUND_INFO);
		}
		return RecipeDetailResponse.of(recipe);
	}

...
}
```

##### 3. 블로그 게시판
BlogController.java 일부
```
@Slf4j
@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {
	private final MemberServiceImp memberServiceImp;
	private final BlogService blogService;

...

	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Long> blogAdd(
			@RequestPart(value = "file", required = false) List<MultipartFile> file,
			@RequestPart BlogRegisterRequest request,
			@AuthenticationPrincipal User user) {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.addBlog(request, file, user.getUsername()));
	}

...

	@DeleteMapping("/{blogId}")
	public ResponseEntity<Long> blogRemove(
			@PathVariable("blogId") Long blogId,
			@AuthenticationPrincipal User user) {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.removeBlog(user.getUsername(), blogId));
	}
}
```

BlogServiceImp.java 일부
```
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BlogServiceImp implements BlogService {
...

	@Override
	public Long addBlog(BlogRegisterRequest request, List<MultipartFile> file, String memberId) {
		Member member = memberRepository.findMemberById(memberId);
		Blog blog = request.toEntity(member);
		/* Image files ****************************/
		List<String> blogImageList = imageService.addFile(file, "blog");
		for(String fn : blogImageList) {
			blogImageRepository.save(BlogImage.builder()
					.imageName(fn)
					.blog(blog)
					.isDeleted(false)
					.build());
		}
		/* Image files */

		return blogRepository.save(blog).getId();
	}

	...

	@Override
	public Long removeBlog(String memberId, Long blogId) {
		Blog blog = blogRepository.findBlogById(blogId);
		if(blog == null) {
			throw new GlobalException(GlobalErrorCode.NOT_FOUND_INFO);
		}
		if(!memberId.equals(blog.getMember().getId())) {
			throw new GlobalException(GlobalErrorCode.NOT_AUTHORIZED_USER);
		}
		List<BlogImage> removeBlogImagesList = blog.getBlogImages();
		for(BlogImage blogImage : removeBlogImagesList) {
			imageService.removeFile(blogImage.getImageName());
			blogImageRepository.delete(blogImage);
		}

		blogRepository.deleteById(blogId);
		return blogId;
	}

...	
}
```

##### 4. 단위 테스트
TokenProviderTest.java 일부
```
@Slf4j
@SpringBootTest
class TokenProviderTest {
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private JwtProperties jwtProperties;

	@DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 생성한다.")
	@Test
	void generateToken() {
		Member testMember = memberRepository.findMemberById("test");

		String token = tokenProvider.generateToken(testMember, Duration.ofDays(14));

		String memberId = Jwts.parser()
				.setSigningKey(jwtProperties.getSecretKey())
				.parseClaimsJws(token)
				.getBody()
				.get("id", String.class);

		assertThat(memberId).isEqualTo(testMember.getId());
	}

...

	@DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
	@Test
	void getAuthentication() {
		String userEmail = "test@test.com";
		String token = JwtFactory.builder()
				.subject(userEmail)
				.build()
				.createToken(jwtProperties);

		Authentication authentication = tokenProvider.getAuthentication(token);

		assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
	}

	@DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
	@Test
	void getUserId() {
		String userId = "test";
		String token = JwtFactory.builder()
				.claims(Map.of("id", userId))
				.build()
				.createToken(jwtProperties);

		String userIdByToken = tokenProvider.getUserId(token);

		assertThat(userIdByToken).isEqualTo(userId);
	}
}
```

## 산출물
### API 명세서
[API명세서](https://oh5chaeyoung.notion.site/API-BBANG-JOA-3d0faf8164064872a5a43c0e0ce68b87?pvs=4)
