var Util = {
	format_date:function(date){
		var month = date.getMonth() + 1;
		month = month<10 ? "0"+month : month;
        return date.getFullYear() + "-" + month + '-' + date.getDate();
	},
};

var Index = {
	render_list:function(date){
		$.getJSON('/datelist', {'date':date}, function(data){
			$('#today').html(juicer($("#tpl_date_tasklist").html(), data));
		});
	},
	
};

$(function(){
	var today = Util.format_date(new Date());

	//init
	Index.render_list(today);

	//bindding
	$('#newPostForm').submit(function(e){
		var content = $('#txtContent').val().trim(); 

		if(content==''){			 
			//$(".alert").alert();
			return false; 
		}
		$.post('/new',{'content':content},function(data){			
			$('#txtContent').val('');
			Index.render_list(today);
		})		 
		return false; //禁止提交后页面刷新
	}); 

});