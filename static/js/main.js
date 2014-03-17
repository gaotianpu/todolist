var Util = {
	format_date:function(date){
		var month = date.getMonth() + 1;
		month = month<10 ? "0"+month : month;
        return date.getFullYear() + "-" + month + '-' + date.getDate();
	},
};

var Index = {
	last_content: '',

	render_list:function(date){
		$.getJSON('/datelist', {'date':date,'r':Math.random()}, function(data){
			$('#today').html(juicer($("#tpl_date_tasklist").html(), data));
		});
	},
	
};

$(function(){
	var today = Util.format_date(new Date());

	//init
	Index.render_list(today);

	//bindding
	$('#newPostForm').submit(function(){
		var content = $('#txtContent').val().trim(); 

		if(content=='' || Index.last_content==content){
			$('#newPostForm').addClass('alert-danger');	
			$('#btnSubmit').removeClass('btn-default').addClass('btn-danger');	
			return false; 
		}

		if(Index.last_content==content){
			//?
			return false;
		} 

		$.post('/new', {'content':content}, function(result){			 
			$("#list_"+today).append(juicer($("#tpl_newtask_form").html(), $.parseJSON(result)));

			$("html,body").animate({
            	scrollTop:$("#navPost").offset().top - 5
            },300);

			Index.last_content = content;
			$('#txtContent').val('');
		})		 
		return false; //禁止提交后页面刷新
	});

	$("#txtContent").keydown(function(){
		var content = $('#txtContent').val().trim(); 
		if(content!=''){
			$('#newPostForm').removeClass('alert-danger');	
			$('#btnSubmit').removeClass('btn-danger').addClass('btn-default');	
		}

	}); 

});