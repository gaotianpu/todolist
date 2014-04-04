;$(function(){
	$('a').click(function(){
		var term = $(this).attr('href');
		$.getJSON('/wordlist',{'term':term},function(data){			
			$("#test").html(juicer($("#tpl_word_tasks").html(), data));  
		});		 
	});
});