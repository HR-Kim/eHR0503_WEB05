package kr.co.ehr.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloBootController {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value="hello/hello_boot_view.do")
	public String helloBootView() {
		LOG.debug("==========================");
		LOG.debug("=@Controller=helloBootView");
		LOG.debug("==========================");
		return "hello/hello_boot";
	}
	
	@RequestMapping(value="template/bootstrap_form_view.do")
	public String bootstrapFormView() {
		LOG.debug("==========================");
		LOG.debug("=@Controller=bootstrapListView");  
		LOG.debug("==========================");
		return "template/bootstrap_form_template";
	}
	
	
	@RequestMapping(value="template/bootstrap_list_view.do")
	public String bootstrapListView() {
		LOG.debug("==========================");
		LOG.debug("=@Controller=bootstrapListView");  
		LOG.debug("==========================");
		return "template/bootstrap_list_template";
	}	
}
