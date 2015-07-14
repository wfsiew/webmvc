var utils = (function() {
	
	function csrfHeader() {
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		
		var cfg = {
				headers: {}
		};
		
		cfg.headers[header] = token;
		return cfg;
	}
	
	return {
		csrfHeader: csrfHeader
	};
}());