package com.taotao.portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.common.json.JSON;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;
import com.taotao.portal.pojo.AD1Node;
import com.taotao.utils.JsonUtils;

/**
 * 
 * 首页展示
 *
 */
@Controller
public class IndexController {
	
	@Value("${AD1_CATEGORY_ID}")
	private Long AD1_CATEGORY_ID;
	@Value("${AD1_WIDTH}")
	private Integer AD1_WIDTH;
	@Value("${AD1_WIDTH_B}")
	private Integer AD1_WIDTH_B;
	@Value("${AD1_HEIGHT}")
	private Integer AD1_HEIGHT;
	@Value("${AD1_HEIGHT_B}")
	private Integer AD1_HEIGHT_B;
	
	
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/index")
	public String showIndex(Model model){
		
		//根据内容ID去查询内容列表 
		List<TbContent> list = contentService.getContentListByCid(AD1_CATEGORY_ID);
		
		//将列表转换为AD1Node列表 
		List<AD1Node> AD1Nodes = new ArrayList<>();
		for(TbContent content : list){
			AD1Node node = new AD1Node();
			node.setAlt(content.getTitle());
			node.setHeight(AD1_HEIGHT);
			node.setHeightB(AD1_HEIGHT_B);
			node.setWidth(AD1_WIDTH);
			node.setWidthB(AD1_WIDTH_B);
			node.setSrc(content.getPic());
			node.setSrcB(content.getPic2());
			node.setHref(content.getUrl());
			AD1Nodes.add(node);
		}

		//把列表转换成json数据
		String ad1Json = JsonUtils.objectToJson(AD1Nodes);
		
		//把json数据传递给页面
		model.addAttribute("ad1", ad1Json);
		return "index";
	}

}
