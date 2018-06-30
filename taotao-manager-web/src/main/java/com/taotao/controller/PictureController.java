package com.taotao.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.taotao.common.utils.JsonUtils;
import com.taotao.utils.FastDFSClient;

/**
 * 图片上传
 * 
 * @author
 *
 */
@Controller
public class PictureController {

	@Value("${IMAGE_SERVER_URL}")

	private String IMAGE_SERVER_URL;

	@RequestMapping("/pic/upload")
	@ResponseBody
	public String picUpload(MultipartFile uploadFile) {
		Map result = new HashMap();
		try {
			String orignalName = uploadFile.getOriginalFilename();
			String extName = orignalName.substring(orignalName.lastIndexOf(".") + 1);

			FastDFSClient fastDFSClient = new FastDFSClient("classpath:resource/client.conf");

			String url = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
			url = IMAGE_SERVER_URL + url;

			result.put("error", 0);
			result.put("url", url);
			return JsonUtils.objectToJson(result);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("error", 1);
			result.put("message", "上传图片失败！");
			return JsonUtils.objectToJson(result);
		}
		// 获取扩展名

	}

}
