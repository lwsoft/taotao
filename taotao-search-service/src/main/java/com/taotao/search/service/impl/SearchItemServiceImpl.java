package com.taotao.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;
import com.taotao.search.service.SearchItemService;

/**
 * 商品数据导入索引库
 * 
 * @author
 *
 */
@Service
public class SearchItemServiceImpl implements SearchItemService {

	@Autowired
	private SearchItemMapper searchItemMapper;

	@Autowired
	private SolrClient solrClient;

	@Override
	public TaotaoResult importItemsToIndex() {

		try {
			// 1、先查询所有商品数据
			List<SearchItem> itemList = searchItemMapper.getSearchItemList();

			SolrInputDocument document = null;
			// 2、遍历商品数据添加到索引库
			for (SearchItem searchItem : itemList) {
				// 创建文档对象
				document = new SolrInputDocument();
				document.setField("id", searchItem.getId());
				document.setField("item_title", searchItem.getTitle());
				document.setField("item_sell_point", searchItem.getSell_point());
				document.setField("item_price", searchItem.getPrice());
				document.setField("item_image", searchItem.getImage());
				document.setField("item_category_name", searchItem.getItem_category_name());
				document.setField("item_desc", searchItem.getItem_desc());
				solrClient.add(document);

			}
			solrClient.commit();
			solrClient.close();
			 return TaotaoResult.ok();  
		} catch (Exception e) {
			e.printStackTrace(); 
			return TaotaoResult.build(500, "导入数据失败！");  
		}

	}

}
