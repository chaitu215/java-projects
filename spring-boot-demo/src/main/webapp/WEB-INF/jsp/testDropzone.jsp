<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">
#dropzone {
	margin-bottom: 3rem;
	max-width: 720px;
	margin-left: auto;
	margin-right: auto;
}

.dropzone {
	border: 2px dashed #0087F7;
	border-radius: 5px;
	background: white;
}

.dropzone .dz-message {
	font-weight: 400;
	text-align: center;
	margin: 2em 0;
}

.dropzone .dz-message .note {
	font-size: 0.8em;
	font-weight: 200;
	display: block;
	margin-top: 1.4rem;
}
</style>
<link href="/static/dropzone/dropzone.css" type="text/css"
	rel="stylesheet" />
<script src="/static/dropzone/dropzone.js" type="text/javascript"></script>
</head>
<body>
	<div id="dropzone">
		<div id="demo-upload" class="dropzone needsclick">
			<div class="dz-message needsclick">
				Drop files here or click to upload.<br /> <span
					class="note needsclick">(This is just a demo dropzone.
					Selected files are <strong>not</strong> actually uploaded.)
				</span>
			</div>
		</div>
	</div>
	<script>
		myDropzone = new Dropzone(".dropzone", {
			url : "/upload/csv",
			maxFiles : 1,
			maxFilesize : 900, //max 100MB
			acceptedFiles : ".csv",
			addRemoveLinks : true,
			success : function(file, response, e) {
				console.log(file.name);
			}
		});
	</script>
</body>
</html>