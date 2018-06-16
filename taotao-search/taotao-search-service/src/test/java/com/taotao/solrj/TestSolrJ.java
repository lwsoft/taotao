package com.taotao.solrj;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrJ {

	@Test
	public void testAddDocument() throws Exception {

		// 创建一个solrClient对象，创建一个HttpSolrClient对象，需要指定solr服务的url
		String urlString = "http://192.168.136.130:8983/solr/new_core";
		SolrClient solrClient = new HttpSolrClient.Builder(urlString).build();

		SolrInputDocument document = new SolrInputDocument();
		// 向文档中添加域，必须有id域，域的名称必须在schema.xml中定义
		document.addField("id", "2222");
		document.addField("item_title", "海尔空调333");
		document.addField("item_sell_point", "送电暖宝一个哦");
		document.addField("item_price", 10000);
		document.addField("item_image", "http://www.123.jpg");
		document.addField("item_category_name", "电器");
		document.addField("item_desc", "这是一款最新的空调，质量好，值得信赖！！");
		// 将document添加到索引库
		solrClient.add(document);
		// 提交
		solrClient.commit();
		solrClient.close();
	}

	@Test
	public void testDeleteDocument() throws Exception {
		// 创建一个solrClient对象，创建一个HttpSolrClient对象，需要指定solr服务的url
		String urlString = "http://192.168.136.130:8983/solr/new_core";
		SolrClient solrClient = new HttpSolrClient.Builder(urlString).build();
		// 通过id来删除文档
		solrClient.deleteById("1111");
		// 提交
		solrClient.commit();
		solrClient.close();
	}

	@Test
	public void deleteDocumentByQuery() throws Exception {
		// 创建一个solrClient对象，创建一个HttpSolrClient对象，需要指定solr服务的url
		String urlString = "http://192.168.136.130:8983/solr/new_core";
		SolrClient solrClient = new HttpSolrClient.Builder(urlString).build();
		// 通过id来删除
		solrClient.deleteByQuery("id:2222");
		// 提交
		solrClient.commit();
		solrClient.close();
	}
	
	
	@Test
	public void queryDocument() throws Exception{
		// 创建一个solrClient对象，创建一个HttpSolrClient对象，需要指定solr服务的url
		String urlString = "http://192.168.136.130:8983/solr/new_core";
		SolrClient solrClient = new HttpSolrClient.Builder(urlString).build();
		//创建一个SolrQuery对象
		SolrQuery query = new SolrQuery();
		//设置查询条件、过滤条件、分页条件、排序条件、高亮
		//query.set("q", "*:*");
		query.setQuery("手机");
		//分页条件
		query.setStart(0);
		query.setRows(10);
		//设置默认搜索域
		query.set("df", "item_keywords");
		//设置高亮
		query.setHighlight(true);
		//高亮显示的域
		query.addHighlightField("item_title");
		query.setHighlightSimplePre("<em>");
		query.setHighlightSimplePost("</em>");
		//执行查询，得到一个Response对象
		QueryResponse response = solrClient.query(query);
		//取查询结果
		SolrDocumentList solrDocumentList = response.getResults();
		//取查询结果总记录数
		System.out.println("查询结果总记录数："+solrDocumentList.getNumFound());
		for(SolrDocument document : solrDocumentList){
			System.out.println(document.getFieldValue("id"));
			//取高亮显示
			Map<String,Map<String,List<String>>> highlighting = response.getHighlighting();
			List<String> list = highlighting.get(document.getFieldValue("id")).get("item_title");
			String itemTitle = "";
			if(list != null && list.size() > 0){
				itemTitle = list.get(0);
			}else {
				itemTitle = (String)document.get("item_title");
			}
			System.out.println(itemTitle);
			System.out.println(document.get("item_sell_point"));
			System.out.println(document.get("item_price"));
			System.out.println(document.get("item_image"));
			System.out.println(document.get("item_category_name"));
			System.out.println("===============================================");
		}
	}
}
