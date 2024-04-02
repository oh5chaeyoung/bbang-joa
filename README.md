# [백엔드] BBANG JOA
## 개요
- 베이킹 레시피를 공유하는 커뮤니티

#### 일정
- 24.01 - present
- 프론트엔드 1명 + 백엔드 1명
- 역할 : 백엔드, CI/CD

## 사용 기술 및 개발 환경
- Cloud : ```AWS EC2(Ubuntu), ElastiCache Redis, S3```
- DB : ```MySQL(RDB)```
- Framework : ```Spring Boot, Spring Security, JUnit, Jenkins```
- Language : ```Java```
- Tool : ```IntelliJ, Git, GitHub```

## 내용
#### 구현 기능
##### 1.  회원관리
- Spring Security
- JWT
- AWS ElastiCache Redis

TokenProvider.java 일부
```java
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
```java
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
```java
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
```java
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
```java
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
- TokenProviderTest.java
- BlogControllerTest.java
<br>

## 산출물
### API 명세서
[API명세서](https://oh5chaeyoung.notion.site/API-BBANG-JOA-3d0faf8164064872a5a43c0e0ce68b87?pvs=4)

### 아키텍처
![bj_architecture](https://github.com/oh5chaeyoung/bj/assets/110815151/3d83ebb8-6a40-4942-8047-ae36601e377f)
