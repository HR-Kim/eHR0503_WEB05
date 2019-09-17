<%@page import="java.util.ArrayList"%>
<%@page import="kr.co.ehr.code.service.Code"%>
<%@page import="java.util.List"%>
<%@page import="kr.co.ehr.cmn.StringUtil"%>
<%@page import="kr.co.ehr.user.service.Search"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	/** 페이지 사이즈 */
	String pageSize   = "10"  ; 	
	/** 페이지 번호 */
	String pageNum    = "1"  ;	
	/** 검색조건 */
	String searchDiv  = "" ;
	/** 검색어 */
	String searchWord = "" ;
	
	
	Search vo = (Search)request.getAttribute("vo");
	if(null !=vo){
		pageSize = StringUtil.nvl(vo.getPageSize()+"","10");
		pageNum = StringUtil.nvl(vo.getPageNum()+"","1");
		searchDiv = StringUtil.nvl(vo.getSearchDiv(),"");
		searchWord = StringUtil.nvl(vo.getSearchWord(),"");
		
	}else{
		pageSize = "10";
		pageNum  = "1";
		searchDiv = "";
		searchWord = "";
	}
	//pageCode	
	List<Code> codeList = (request.getAttribute("codeList")==null)?(List<Code>)new ArrayList<Code>():(List<Code>)request.getAttribute("codeList");
			
	//userSearch	
	List<Code> codeSearchList = 
	(request.getAttribute("codeSearchList")==null)?
			(List<Code>)new ArrayList<Code>():
				(List<Code>)request.getAttribute("codeSearchList");		
	
	int maxNum      = 0;
    int bottomCount = 10;
    int currPageNo  = 1; //pageNum
    int rowPerPage  = 10;//pageSize
    String url      = request.getContextPath()+"/user/get_retrieve.do";
    String scriptName = "search_page";
    
	String iTotalCnt =(request.getAttribute("totalCnt")==null)?"0":request.getAttribute("totalCnt").toString();
    
	maxNum = Integer.parseInt(iTotalCnt);
	currPageNo = Integer.parseInt(pageNum);
	rowPerPage = Integer.parseInt(pageSize);
			
			

%>
<c:set var="context" value="${pageContext.request.contextPath }" />
<%--
  /**
  * @Class Name : list_template.jsp
  * @Description : bootstrap list
  * @Modification Information
  *
  *   수정일                   수정자                      수정내용
  *  -------    --------    ---------------------------
  *  2018.04.26            최초 생성
  *
  * author SIST 개발팀
  * since 2018.04.26
  *
  * Copyright (C) 2009 by KandJang  All right reserved.
  */
--%>

<html lang="ko">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- 위 3개의 메타 태그는 *반드시* head 태그의 처음에 와야합니다; 어떤 다른 콘텐츠들은 반드시 이 태그들 *다음에* 와야 합니다 -->
<title>사용자관리</title>

<!-- 부트스트랩 -->
<link href="${context}/resources/css/bootstrap.min.css" rel="stylesheet">

<!-- IE8 에서 HTML5 요소와 미디어 쿼리를 위한 HTML5 shim 와 Respond.js -->
<!-- WARNING: Respond.js 는 당신이 file:// 을 통해 페이지를 볼 때는 동작하지 않습니다. -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->


</head>
<body>

	<!-- div container -->
	<div class="container">
		<!-- div title -->
		<div class="page-header">
			<h1>사용자관리</h1>
		</div>
		<!--// div title -->

		<!-- 검색영역 -->
		<div class="row">
			<div class="col-md-12 text-right">
				<form class="form-inline" name="frm" id="frm" method="get" 
				   action="${context}/user/get_retrieve.do">
				   <input type="hidden" name="pageNum" id="pageNum">
					<div class="form-group">
					    <%=StringUtil.makeSelectBox(codeList, "pageSize", pageSize, false) %>
					    <%=StringUtil.makeSelectBox(codeSearchList, "searchDiv", searchDiv, true) %>
						<input type="text" class="form-control input-sm" id="searchWord" value="${vo.searchWord}" name="searchWord" placeholder="검색어" />
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<button type="button" class="btn btn-default btn-sm"
							id="doRetrieve" >조회</button>
						<select class="form-control input-sm" id="ext" name="ext">
							<option value="xlsx">xlsx</option>
							<option value="csv">csv</option>
						</select>	
						<input type="button" class="btn btn-default btn-sm" id="doExcel"
							value="엑셀다운" />
					</div>
				</form>
			</div>
		</div>
		<!--// 검색영역 -->

		<!-- Grid영역 -->
		<div class="table-responsive">
			<table class="table  table-striped table-bordered table-hover" id="listTable">
				<thead class="bg-primary">
				    <th class="text-center col-md-1 col-xs-1">
				    <input type="checkbox" id="checkAll" name="checkAll" onclick="checkAll();"></th>
					<th class="text-center col-md-1 col-xs-1">번호</th>
					<th class="text-center col-md-4 col-xs-4 ">ID</th>
					<th class="text-center col-md-4 col-xs-3">이름</th>
					<th class="text-center col-md-1 col-xs-1">레벨</th>
					<th class="text-center col-md-1 col-xs-1">추천</th>
					<th class="text-center col-md-1 col-xs-1">날짜</th>
				</thead>

				<tbody>
					<c:choose>
						<c:when test="${list.size()>0 }">
							<c:forEach  var="user"  items="${list}">
								<tr>
								    <td class="text-center"><input type="checkbox" name="check"></td>
									<td class="text-center"><c:out value="${user.num}"/></td>
									<td class="text-left"><c:out value="${user.u_id}"/></td>
									<td class="text-left"><c:out value="${user.name}"/></td>
									<td class="text-left"><c:out value="${user.hLevel}"/></td>
									<td class="text-right"><c:out value="${user.recommend}"/></td>
									<td class="text-center"><c:out value="${user.regDt}"/></td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="99">등록된 게시글이 없습니다.</td>
							</tr>
						</c:otherwise>	

					</c:choose>
					
				</tbody>
			</table>
		</div>
		<!--// Grid영역 -->

		<!-- pagenation -->
		<div class="text-center">
			<%=StringUtil.renderPaing(maxNum, currPageNo, rowPerPage, bottomCount, url, scriptName) %>
		</div>
		<!--// pagenation -->

		<!-- 입력 Form -->
		<div class="container"> 
			<div class="col-lg-12"></div>
		    <div class="col-lg-12"></div>
		    <div class="panel panel-default"></div>
		    <!-- Button Area -->
			<div class="row">
			    <div class="col-lg-10 col-sm-10 col-xs-10">
			        <div class="text-right">
						<button type="button" class="btn btn-default btn-sm" id="doInit">초기화</button>
						<button type="button" class="btn btn-default btn-sm" id="doSave">등록</button>
						<button type="button" class="btn btn-default btn-sm" id="doUpdate">수정</button>
						<button type="button" class="btn btn-default btn-sm" id="doDelete">삭제</button>
					</div>
				</div>
			</div> 
			<br/>
		    <!-- 입력 form -->
		    <form action="do_update.do" name="frmEdit" id="frmEdit" method="post" class="form-horizontal">
				<div class="form-group">
					<label for="u_id" class="col-sm-2 control-label">아이디</label>
					<div class="col-sm-8">
						<input type="text" maxlength="20"  class="form-control input-sm" id="u_id" placeholder="아이디" name="u_id" disabled="disabled">
					</div>
				</div>
				<div class="form-group">
					<label for="regDt" class="col-sm-2 control-label">등록일</label>
					<div class="col-sm-8">
						<input type="text" maxlength="20" disabled="disabled"  class="form-control input-sm" id="regDt" placeholder="등록일" name="regDt">
					</div>
				</div>
								
				<div class="form-group">
					<label for="name" class="col-sm-2 control-label">이름</label>
					<div class="col-sm-8">
						<input type="text" maxlength="300"  class="form-control input-sm" id="name" placeholder="이름" name="name">
					</div>
				</div>
				<div class="form-group">
					<label for="passwd" class="col-sm-2 control-label">비밀번호</label>
					<div class="col-sm-8">
						<input type="password" maxlength="100"  class="form-control input-sm" id="passwd" placeholder="비밀번호" name="passwd">
					</div>
				</div>	
				<div class="form-group">
					<label for="hLevel" class="col-sm-2 control-label">레벨</label>
					<div class="col-sm-8">
						<select class="form-control" id="hLevel" name="hLevel">
							<option value="1">BASIC</option>
							<option value="2">SILBER</option>
							<option value="3">GOLD</option>
						</select>						
					</div>
				</div>
				
				<div class="form-group">
					<label for="login" class="col-sm-2 control-label">로그인</label>
					<div class="col-sm-8">
						<input type="text" maxlength="5"  class="form-control input-sm" id="login" placeholder="login" name="로그인">
					</div>
				</div>
				<div class="form-group">
					<label for="recommend" class="col-sm-2 control-label">추천</label>
					<div class="col-sm-8">
						<input type="text" maxlength="5"  class="form-control input-sm" id="recommend" placeholder="recommend" name="추천">
					</div>
				</div>	
				<div class="form-group">
					<label for="email" class="col-sm-2 control-label">이메일</label>
					<div class="col-sm-8">
						<input type="text" maxlength="320"  class="form-control input-sm" id="email" placeholder="email" name="email">
					</div>
				</div>																						
		    </form>
		    <!--// 입력 form -->
		</div>
		<!--// 입력 Form -->

	</div>
	<!--// div container -->
	<!-- jQuery (부트스트랩의 자바스크립트 플러그인을 위해 필요합니다) -->
	<script src="${context}/resources/js/jquery-1.12.4.js"></script>
	<!-- 모든 컴파일된 플러그인을 포함합니다 (아래), 원하지 않는다면 필요한 각각의 파일을 포함하세요 -->
	<script src="${context}/resources/js/bootstrap.min.js"></script>
	<script type="text/javascript">
		//paging이동
		function search_page(url,pageNum){
			//alert(url+"|"+pageNum);
			var frm = document.frm;
			frm.pageNum.value = pageNum;
			frm.action = url;
			frm.submit();
		}
		   
		function doExcelDown(){
			var frm = document.frm;
			frm.action = "${context}/user/exceldown.do";
			frm.submit();
		}
		
		$("#doExcel").on("click",function(){
			if(false==confirm("엑셀다운 하시겠습니까?"))return;
			doExcelDown();
		});
	
	    //수정
 		$("#doUpdate").on("click",function(){
	    	
	    	//intLevel -> Level
	    	var intLevel ="BASIC";
	    	if($("#hLevel").val()=="1"){
	    		intLevel ="BASIC";
	    	}else if($("#hLevel").val()=="2"){
	    		intLevel ="SILVER";
	    	}else if($("#hLevel").val()=="3"){
	    		intLevel ="GOLD";
	    	}
	    	
	    	
	    	if(false==confirm("수정 하시겠습니까?"))return;
	    	
			$.ajax({
	            type:"POST",
	            url:"${context}/user/do_update.do",
	            dataType:"html",// JSON
	            data:{
	            	"u_id": $("#u_id").val(),
	            	"name": $("#name").val(),
	            	"passwd": $("#passwd").val(),
	            	"hLevel": intLevel,
	            	"login": $("#login").val(),
	            	"recommend": $("#recommend").val(),
	            	"email": $("#email").val()
	            },
	            success: function(data){//통신이 성공적으로 이루어 졌을때 받을 함수
	                //console.log(data);
	            	//{"msgId":"1","msgMsg":"삭제 되었습니다.","totalCnt":0,"num":0}
	            	var parseData = $.parseJSON(data);
	            	if(parseData.msgId=="1"){
	            		alert(parseData.msgMsg);
	            		doRetrieve();
	            	}else{
	            		alert(parseData.msgMsg);
	            	}
	            	

	            	
	            },
	            complete: function(data){//무조건 수행
	             
	            },
	            error: function(xhr,status,error){
	             
	            }
	        });		    	
	    });
	    
		//등록
	    $("#doSave").on("click",function(){
	    	
	    	//intLevel -> Level
	    	var intLevel ="BASIC";
	    	if($("#hLevel").val()=="1"){
	    		intLevel ="BASIC";
	    	}else if($("#hLevel").val()=="2"){
	    		intLevel ="SILVER";
	    	}else if($("#hLevel").val()=="3"){
	    		intLevel ="GOLD";
	    	}
	    	
	    	
	    	if(false==confirm("등록 하시겠습니까?"))return;
	    	
			$.ajax({
	            type:"POST",
	            url:"${context}/user/do_insert.do",
	            dataType:"html",// JSON
	            data:{
	            	"u_id": $("#u_id").val(),
	            	"name": $("#name").val(),
	            	"passwd": $("#passwd").val(),
	            	"hLevel": intLevel,
	            	"login": $("#login").val(),
	            	"recommend": $("#recommend").val(),
	            	"email": $("#email").val()
	            },
	            success: function(data){//통신이 성공적으로 이루어 졌을때 받을 함수
	                //console.log(data);
	            	//{"msgId":"1","msgMsg":"삭제 되었습니다.","totalCnt":0,"num":0}
	            	var parseData = $.parseJSON(data);
	            	if(parseData.msgId=="1"){
	            		alert(parseData.msgMsg);
	            		doRetrieve();
	            	}else{
	            		alert(parseData.msgMsg);
	            	}
	            	

	            	
	            },
	            complete: function(data){//무조건 수행
	             
	            },
	            error: function(xhr,status,error){
	             
	            }
	        });		    	
	    });
	
		//삭제
		$("#doDelete").on("click",function(){
			console.log($("#u_id").val());
			
			
			if($("#u_id").val()=="" || $("#u_id").val()==false){
				alert("삭제할 데이터를 선택 하세요.");
				return;
			}
			
			if(confirm("삭제 하시겠습니까?") == false)return;
			
			

			$.ajax({
	            type:"POST",
	            url:"${context}/user/do_delete.do",
	            dataType:"html",// JSON
	            data:{
	            	"u_id": $("#u_id").val()
	            },
	            success: function(data){//통신이 성공적으로 이루어 졌을때 받을 함수
	                //console.log(data);
	            	//{"msgId":"1","msgMsg":"삭제 되었습니다.","totalCnt":0,"num":0}
	            	var parseData = $.parseJSON(data);
	            	if(parseData.msgId=="1"){
	            		alert(parseData.msgMsg);
	            		doRetrieve();
	            	}else{
	            		alert(parseData.msgMsg);
	            	}
	            	

	            	
	            },
	            complete: function(data){//무조건 수행
	             
	            },
	            error: function(xhr,status,error){
	             
	            }
	        });	    				
		});
	
		//초기화
		$("#doInit").on("click",function(){
    		$("#u_id").val("");
         	$("#regDt").val("");
        	$("#name").val("");
        	$("#passwd").val("");
        	$("#hLevel").val("1");
        	$("#login").val("");
        	$("#recommend").val("");
        	$("#email").val(""); 
        	
        	$("#u_id").prop("disabled",false);
        	$("#doSave").prop("disabled",false);
        	$("#doUpdate").prop("disabled",true);
		});
	
	
	    //그리드 클릭 : 아이디 단건조회
	    //listTable
	    $("#listTable>tbody").on("click","tr",function(){
	    	console.log("#listTable>tbody");
	    	var tr  = $(this);
	    	var td  =  tr.children();
	    	console.log("td:"+td.text());
	    	var uId = td.eq(2).text();
	    	console.log("uId:"+uId);
	    	$.ajax({
	            type:"POST",
	            url:"${context}/user/get_select_one.do",
	            dataType:"html",// JSON
	            data:{
	            	"u_id": uId
	            },
	            success: function(data){//통신이 성공적으로 이루어 졌을때 받을 함수
	                console.log(data);
	            	//{"u_id":"j05_1366","name":"김민석05","passwd":"1234","hLevel":"GOLD","login":99,"recommend":99,"email":"guntwoo@naver.com","regDt":"2019/09/10","totalCnt":1,"num":1}
	            	var parseData = $.parseJSON(data);
	            	console.log(parseData.u_id)
	        		$("#u_id").val(parseData.u_id);
	            	$("#regDt").val(parseData.regDt);
	            	$("#name").val(parseData.name);
	            	$("#passwd").val(parseData.passwd);
	            	$("#hLevel").val(parseData.level);
	            	$("#login").val(parseData.login);
	            	$("#recommend").val(parseData.recommend);
	            	$("#email").val(parseData.email);
	            	//disabled  
	            	$("#u_id").prop("disabled",true);
	            	$("#doSave").prop("disabled",true);
	            	$("#doUpdate").prop("disabled",false);
	            	
	            },
	            complete: function(data){//무조건 수행
	             
	            },
	            error: function(xhr,status,error){
	             
	            }
	        });	    	
	    	
	    });
	    
	    
	    
	    
	    
		//check전체 선택 및 해제
		function checkAll(){
			console.log("checkAll");
			if($("#checkAll").is(':checked')==true){
				$("input[name='check']").prop("checked",true);//check
			}else{
				$("input[name='check']").prop("checked",false);//check해제
			}
		}
	
		function doRetrieve(){
			var frm = document.frm;
			frm.pageNum.value= 1;
			frm.action = "${context}/user/get_retrieve.do";
			frm.submit();
		}
	
		//Enter Event
		$("#searchWord").keydown(function(key) {
			if (key.keyCode == 13) {
				doRetrieve();
			}
		});
	
		//조회
		$("#doRetrieve").on("click",function(){
			console.log("doRetrieve");
			doRetrieve();
		}); 
		
		
	
		$(document).ready(function() {
			//alert("ready");
		});
	</script>
</body>
</html>