package com.taotao.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ItemAddMessageListener implements MessageListener {
	
	@Autowired	
	private ItemService itemService;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	
	@Value("${HTML_OUT_PATH}")
	private String HTML_OUT_PATH;

	@Override
	public void onMessage(Message message) {
		try {
			// 从消息中取出商品id
			TextMessage textMessage = (TextMessage) message;
			String strId;

			strId = textMessage.getText();
			Thread.sleep(1000);
			Long itemId = Long.parseLong(strId);
			TbItem tbItem = itemService.getItemById(itemId);
			Item item = new Item(tbItem);
			
			TbItemDesc itemDesc = itemService.getTbItemDescById(itemId);
			
			Configuration configuration = freeMarkerConfigurer.getConfiguration();
			
			Template template = configuration.getTemplate("item.ftl");
			
			Map data = new HashMap<>();
			data.put("item", item);
			data.put("itemDesc", itemDesc);
			
			Writer out = new FileWriter(new File(HTML_OUT_PATH+strId+".html"));  
			template.process(data, out);  
			out.close();  
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
