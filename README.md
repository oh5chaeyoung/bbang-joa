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

	...

	/* Member 정보와 토큰만료기간이 들어오면 JWT를 생성한다 */
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

	/* JWT가 들어오면 Authentication을 제공한다 */
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

  	/* 레시피 전체 리스트를 제공한다(모든 사용자 접근 가능) */
	@GetMapping("")
	public ResponseEntity<List<RecipeListResponse>> recipeList() {
		return ResponseEntity.status(HttpStatus.OK).body(recipeService.getAllRecipes());
	}

  	/* 레시피 아이디가 들어오면 상세 페이지를 제공한다(모든 사용자 접근 가능) */
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
	...

  	/* 레시피 전체 리스트를 제공한다(모든 사용자 접근 가능) */
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

  	/* 레시피 아이디가 들어오면 상세 페이지를 제공한다(모든 사용자 접근 가능) */
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
    	...

  	/* 블로그 글 정보와 로그인한 사용자 정보가 들어오면 블로그 글을 등록한다 (로그인한 사용자만 접근 가능) */
	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Long> blogAdd(
			@RequestPart(value = "file", required = false) List<MultipartFile> file,
			@RequestPart BlogRegisterRequest request,
			@AuthenticationPrincipal User user) {
		return ResponseEntity.status(HttpStatus.OK).body(blogService.addBlog(request, file, user.getUsername()));
	}

    	...

  	/* 블로그 아이디와 로그인한 사용자 정보가 들어오면 블로그 글을 삭제한다 (작성자만 접근 가능) */
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

  	/* 블로그 글 정보와 로그인한 사용자 정보가 들어오면 블로그 글을 등록한다 (로그인한 사용자만 접근 가능) */
	@Override
	public Long addBlog(BlogRegisterRequest request, List<MultipartFile> file, String memberId) {
		Member member = memberRepository.findMemberById(memberId);
		Blog blog = request.toEntity(member);

		/* Image files */
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

  	/* 블로그 아이디와 로그인한 사용자 정보가 들어오면 블로그 글을 삭제한다 (작성자만 접근 가능) */
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
package com.sweetievegan.config.jwt;

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

BlogControllerTest.java 일부

```
package com.sweetievegan.blog.controller;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class BlogControllerTest {
	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	BlogRepository blogRepository;
	@Autowired
	BlogImageRepository blogImageRepository;
	@Autowired
	private TokenProvider tokenProvider;

	Member member;

	@BeforeEach
	void setSecurityContext() {
		member = memberRepository.findMemberById("test");

		String token = tokenProvider.generateToken(member, Duration.ofDays(14));

		Authentication authentication = tokenProvider.getAuthentication(token);

		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);
	}

	@BeforeEach
	public void mockMvcSetUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@BeforeEach
	void deleteBlogs() { ... }

	/* CRUD TEST */
	@DisplayName("addBlog : 블로그 글 등록에 성공한다.")
	@Test
	void addBlog() throws Exception {
		final String url = "/blogs";
		int num = createRandomNumber();
		BlogRegisterRequest requestDto = BlogRegisterRequest.builder()
				.title("title_" + num)
				.content("content_" + num)
				.tags("tag_" + num)
				.summary("summary_" + num)
				.build();
		String requestJson = new ObjectMapper().writeValueAsString(requestDto);
		MockMultipartFile request = new MockMultipartFile("request", "request", "application/json", requestJson.getBytes(StandardCharsets.UTF_8));

		MockMultipartFile file1 = new MockMultipartFile("file", "testFile1.txt", "text/plain", "Test file content 1".getBytes());

		mockMvc.perform(multipart(url)
					.file(file1)
					.file(request))
			.andExpect(status().isOk());
	}

	@DisplayName("modifyBlog : 블로그 글 수정에 성공한다.")
	@Test
	void modifyBlog() throws Exception {
		final String url = "/blogs/{blogId}";
		int num = createRandomNumber();
		Blog savedBlog = createDefaultBlog();
		BlogRegisterRequest requestDto = BlogRegisterRequest.builder()
				.title("modified title_" + num)
				.content("modified content_" + num)
				.tags("modified tag_" + num)
				.summary("modified summary_" + num)
				.build();
		String requestJson = new ObjectMapper().writeValueAsString(requestDto);
		MockMultipartFile request = new MockMultipartFile("request", "request", "application/json", requestJson.getBytes(StandardCharsets.UTF_8));

		MockMultipartFile file1 = new MockMultipartFile("file", "testFile1.txt", "text/plain", "Test file content 1".getBytes());

		mockMvc.perform(MockMvcRequestBuilders
					.multipart(HttpMethod.PUT, url, savedBlog.getId())
					.file(file1)
					.file(request))
			.andExpect(status().isOk()) ;
	}

	@DisplayName("findAllBlogs : 블로그 목록 조회에 성공한다.")
	@Test
	void findAllBlogs() throws Exception {
		final String url = "/blogs";
		Blog savedBlog = createDefaultBlog();

		final ResultActions resultActions = mockMvc.perform(get(url)
							.accept(MediaType.APPLICATION_JSON));
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].title").value(savedBlog.getTitle()));
	}

	@DisplayName("findBlog : 블로그 글 조회에 성공한다.")
	@Test
	void findBlog() throws Exception {
		final String url = "/blogs/{blogId}";
		Blog savedBlog = createDefaultBlog();

		final ResultActions resultActions = mockMvc.perform(get(url, savedBlog.getId()));

		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value(savedBlog.getContent()));
	}

	@DisplayName("deleteBlog : 블로그 글 삭제에 성공한다.")
	@Test
	void deleteBlog() throws Exception {
		final String url = "/blogs/{blogId}";
		Blog savedBlog = createDefaultBlog();

		mockMvc.perform(delete(url, savedBlog.getId()))
				.andExpect(status().isOk()) ;
	}

	...

}
```

## 산출물
### API 명세서
[API명세서](https://oh5chaeyoung.notion.site/API-BBANG-JOA-3d0faf8164064872a5a43c0e0ce68b87?pvs=4)
