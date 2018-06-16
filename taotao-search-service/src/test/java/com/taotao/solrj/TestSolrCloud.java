package com.taotao.solrj;

import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrCloud {

	@Test
	public void testSolrCloudAddDocument() throws Exception {
		// 创建一个CloudSolrClient对象，构造方法中需要指定zookeeper的地址列表
		CloudSolrClient cloudSolrClient = new CloudSolrClient.Builder()
				.withZkHost("192.168.136.128:2181,192.168.136.129:2181,192.168.136.130:2181").build();
		// 需要设置默认的Collection
		cloudSolrClient.setDefaultCollection("collection");
		// 创建一个文档对象
		SolrInputDocument document = new SolrInputDocument();
		// 向文档中添加域
		document.addField("id", "11111");
		document.addField("item_title", "测试商品名称");
		document.addField("item_price", 150);
		document.addField("item_image", "http://www.baidu.123.jpg");

		final UpdateResponse updateResponse = cloudSolrClient.add(document);
		// NamedList<?> ns = updateResponse.getResponse();
		// Indexed documents must be committed
		cloudSolrClient.commit("collection");

//		// 把文档写入索引库
//		cloudSolrClient.add(document);
//		// 提交
//		cloudSolrClient.commit();
	}
}
