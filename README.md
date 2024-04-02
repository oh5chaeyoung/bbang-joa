# [백엔드] BBANG JOA
## 개요
- 베이킹 레시피를 공유하는 커뮤니티

#### 일정
- 24.01 - present
- 프론트엔드 1명 + 백엔드 1명
- 역할 : 백엔드, CI/CD

## 사용 기술 및 개발 환경
- Cloud : ```AWS EC2(Ubuntu), ElastiCache Redis, S3```
- DB : ```MySQL(RDB), Redis```
- Framework : ```Spring Boot, Spring Security, JUnit, Jenkins```
- Language : ```Java```
- Tool : ```IntelliJ, Git, GitHub```

## 내용
#### 구현 기능
##### 1.  회원관리
- 이메일 인증 코드 관리하기(전송, 확인)
- 이메일 중복 확인하기
- 회원가입하기
- 회원탈퇴하기
- 로그인(JWT 발급)
- 구글 소셜 로그인(JWT 발급)

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
- 전체 목록보기
- 상세 페이지보기
- 작성하기
- 수정하기
- 삭제하기
- 검색하기

##### 3. 블로그 게시판
- 전체 목록보기
- 상세 페이지보기
- 작성하기
- 수정하기
- 삭제하기
- 검색하기

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

### 트러블슈팅
[AWS ElastiCache Redis 적용하기](https://ripe-cheese-d1e.notion.site/O-X-47abd3ebabd94ba7b79f48b73818cb9d?pvs=4)
