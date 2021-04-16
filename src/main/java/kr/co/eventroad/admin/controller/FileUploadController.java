package kr.co.eventroad.admin.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import kr.co.eventroad.admin.common.CommonController;
import kr.co.eventroad.admin.common.Constant;
import kr.co.eventroad.admin.common.FileUploadForm;

@Controller
public class FileUploadController {
	 @RequestMapping(value = "/show.do")
	    public String displayForm() {
	        return "file_upload_form";
	    }
	     
	    @RequestMapping(value = "/save.do")
	    public String save(@ModelAttribute("uploadForm") FileUploadForm uploadForm, Model model) {
	         
	        List<MultipartFile> files = uploadForm.getFiles();
	 
	        List<String> fileNames = new ArrayList<String>();
	         
	        if(null != files && files.size() > 0) {
	            for (MultipartFile multipartFile : files) {
	 
	                String fileName = multipartFile.getOriginalFilename();
	                fileNames.add(fileName);

	                //Handle file content - multipartFile.getInputStream()
	                try {
						CommonController.saveImage(Constant.IMAGE_PROFILE_IMAGE_UPLOAD + multipartFile.getOriginalFilename(), multipartFile);
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	        }
			Map<String, Object> retMap = new HashMap<String, Object>();
			retMap.put("MSG", Constant.MSG_OK);
			retMap.put("CODE", Constant.CODE_OK);


			model.addAttribute("retMap", retMap);
			
	        return "json";
	    }
}
