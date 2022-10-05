<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">	
<title>Insert title here</title>
<% 
String context = request.getContextPath();
%>
<c:import url="${context}/header.jsp"></c:import>


<script src="http://code.jquery.com/jquery-latest.min.js"></script>

<!-- 커뮤니티 무한스크롤 ajax-->
<script type="text/javascript">
var count = 2;
$(window).scroll(function(){
	if($(window).scrollTop() == $(document).height() - $(window).height()) {	
		$.ajax({ 
			url: "<%=context%>/commuListScroll.do?pageNum="+count,
			success: function(result){
						$("#scroll").append(result);
						count = count+1;
					}
		});
	}
});	
console.log(count);
		
</script>

<!-- 삭제버튼 confirm -->
<%-- <script type="text/javascript">
function ConfirmDel() {
	if (confirm("삭제 하시겠습니까?")) {
		location.href="<%=context%>/commuDeleteForm.do?c_num=${commu.c_num}&pageNum=${currentPage}";
	} else {
		return false;
	}
}
</script> --%>
</head>
<body>

<!-- main start -->	
	<main style="margin-top: 148px;">
		<!-- main 상단 고정이미지 -->
		<section class=" container-fluid py-5 text-center bg-img" style="height:300px; background-image: url('dh_images/bgimg.jpg');">
			<div class="mt-5 row">
				<div class="col">
					<p class="mt-2 text-white">자유롭게 당신의 일상을 공유하세요</p>
					<p class="text-white" style="font-size: 60px; font-weight: bold;">커뮤니티</p>
				</div>
			</div>	
		</section>
		<!-- 검색창 -->
		<section class="container">
			<div class="row m-5 justify-content-md-center">	
				<div	
					class="col-md-auto Search__SearchInputWrappper-sc-1ef83fv-0 beOSqn">
					<span
					class="CommonIconSet__InitialIcon-sc-15eoam-0 CommonIconSet__MagnifierGrayIconContent-sc-15eoam-1 jZNHYY QjNCN" style="margin-left: 5px;"></span>
					<button style="margin-left: 10px;"></button>
					<input placeholder="동행을 찾아보세요!" value="">
				</div>
				<div class="com-md-auto">
					<a class="btn btn-success" role="button" href="<%=context%>/commuWriteForm.do">글쓰기</a>
				</div>
			</div>	
			<!-- list start -->
			<c:if test="${totCnt > 0 }">
				<c:forEach var="commu" items="${list }" varStatus="status">	
					<div class="row m-5 justify-content-md-center">	
						
						<div class="col-md-auto">
							<div class="card shadow-sm">
								<div class="card-header d-flex">
									<img class="mt-1 mb-1 img-fluid rounded-circle" alt="회원이미지" src="dh_images/user.png" style="height: 30px; margin-right: 5px;">
									<span class="mt-2">${commu.user_id }</span>
									<!-- dropdown menu는 작성자만 수정 삭제 접근가능, else alert("작성자만 수정 삭제 가능합니다.") -->
									<div class="dropdown ms-auto">
								  		<button class="btn btn-icon-only" type="button" data-bs-toggle="dropdown" aria-expanded="false">
								  			<span class="bi bi-three-dots-vertical"></span>
								  		</button>
								  		<ul class="dropdown-menu">
								    		<li><a class="dropdown-item" href="<%=context%>/commuUpdateForm.do?c_num=${commu.c_num}&pageNum=${currentPage}">수정</a></li>
								   			<li><a class="dropdown-item" data-bs-toggle="modal" data-bs-target="#deleteModal" >삭제</a></li><!-- href='javascript:void(0);confirm();' onclick="ConfirmDel(); -->
								  		</ul>
									</div>
								</div>
								<button class="nav-link" data-remote="<%=context%>/commuContent.do?c_num=${commu.c_num}&pageNum=${currentPage}" class="" data-bs-toggle="modal" data-bs-target=".bd-modal-xl">
			<!-- 게시글이미지 -->	<img class="rounded card-img-top" src="${imgList[status.index].c_img_path }">
								</button>								
								<div class="card-body d-flex">
									<p class="card-text">${commu.c_content }</p>
									
								</div>
								<div class="card-footer d-flex">
									<p class="mt-2">#${commu.c_hash }</p>
									<span class="ms-auto mt-2">${commu.c_date }</span>
								</div>
							</div>
						</div>
						<!-- Modal page-->
						<div class="col-md-auto modal bd-modal-xl fade" tabindex="-1" role="dialog" aria-labelledby="bd-modal-xl" aria-hidden="true">
							<div class="modal-dialog modal-dialog-centered modal-xl">
								<div class="modal-content">

								</div>
							</div>
					    </div>
					    <!-- deleteModal -->
	<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="exampleModalLabel">게시글 삭제</h5>
	        <button type="button" class="close" data-bs-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body">
	        게시글을 정말로 삭제하시겠습니까?
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">취소</button>
	        <button type="button" class="btn btn-primary" onclick="location.href='<%=context%>/commuDeletePro.do?c_num=${commu.c_num}&pageNum=${currentPage}'">삭제</button>
	      </div>
	    </div>
	  </div>
	</div>
						<!-- end modal -->
						<!-- Call modalContent.jsp Script -->
						<script>
							$('.bd-modal-xl').on('show.bs.modal', function(e) {
						
								var button = $(e.relatedTarget);
								var modal = $(this);
								
								modal.find('.modal-content').load(button.data("remote"));
						
							});
						</script>
					</div>
				</c:forEach>
			</c:if>
			<c:if test="${totCnt == 0 }">
				<h1>데이터가 없네</h1>
			</c:if>
			<!-- end list -->
			<div id="scroll"></div>
		</section>
	</main>
	
	
	
	<footer class="py-5 bg-dark" style="margin-top: 100px;">
		<div class="container">
			<p class="m-0 text-center text-white">Copyright &copy; Your
				Website 2022</p>
		</div>
	</footer>
</body>
</html>