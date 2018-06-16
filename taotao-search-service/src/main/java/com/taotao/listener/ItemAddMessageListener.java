package com.taotao.listener;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.common.pojo.SearchItem;
import com.taotao.search.mapper.SearchItemMapper;

/**
 * 监听商品添加页面
 * 
 * @author
 *
 */
public class ItemAddMessageListener implements MessageListener {

	@Autowired
	private SearchItemMapper searchItemMapper;
	@Autowired
	private SolrClient solrClient;
	
	@Override
	public void onMessage(Message message) {
		try{
			//从消息中取出商品id
			TextMessage textMessage = (TextMessage) message;
			String text = textMessage.getText();
			long itemId = Long.parseLong(text);
			//根据商品id查询数据
			//等待事务 提交
			Thread.sleep(1000);
			SearchItem searchItem = searchItemMapper.getItemById(itemId);
			
			// 创建文档对象
			SolrInputDocument document = new SolrInputDocument();
			document.setField("id", searchItem.getId());
			document.setField("item_title", searchItem.getTitle());
			document.setField("item_sell_point", searchItem.getSell_point());
			document.setField("item_price", searchItem.getPrice());
			document.setField("item_image", searchItem.getImage());
			document.setField("item_category_name", searchItem.getItem_category_name());
			document.setField("item_desc", searchItem.getItem_desc());
			solrClient.add(document);
			solrClient.commit();
			solrClient.close();

			
		}catch(Exception e){
			e.printStackTrace();
		}
	
		

	}

}
