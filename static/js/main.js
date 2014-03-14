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
			$('#list_' + date).html(juicer($("#tpl_tasklist").html(), data));
		});
	},
	
};

$(function(){
	var today = Util.format_date(new Date());

	//init
	Index.render_list(today);

	//bindding
	$('#newPostForm').submit(function(e){
		$.post('/new',{'content':$('#txtContent').val()},function(data){			
			$('#txtContent').val('');
			Index.render_list(today);
		})		 
		return false; //禁止提交后页面刷新
	}); 

});