package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.IDUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Resource(name = "itemAddTopic")
	private Destination destination;

	@Autowired
	private JedisClient jedisClient;

	@Value("${ITEM_INFO}")
	private String ITEM_INFO;
	@Value("${ITEM_EXPIRE}")
	private Integer ITEM_EXPIRE;

	@Override
	public TbItem getItemById(Long itmeId) {
		// 先查询缓存
		try {
			String json = jedisClient.get(ITEM_INFO + ":" + itmeId + ":BASE");
			if (!StringUtils.isBlank(json)) {
				// 把json转换成对象
				return JsonUtils.jsonToPojo(json, TbItem.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 查询数据库
		TbItem tbItem = itemMapper.selectByPrimaryKey(itmeId);

		try {
			// 把查询结果添加到缓存
			jedisClient.set(ITEM_INFO + ":" + itmeId + ":BASE", JsonUtils.objectToJson(tbItem));
			// 设置过期时间，提高缓存的利用率
			jedisClient.expire(ITEM_INFO + ":" + itmeId + ":BASE", ITEM_EXPIRE);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tbItem;
	}

	@Override
	public EasyUIDataGridResult getItemList(int page, int pageSize) {
		// 设置分页信息
		PageHelper.startPage(page, pageSize);
		// 执行查询
		TbItemExample tbItemExample = new TbItemExample();

		List<TbItem> list = this.itemMapper.selectByExample(tbItemExample);
		// 取查询结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		EasyUIDataGridResult resultList = new EasyUIDataGridResult();
		resultList.setRows(list);
		resultList.setTotal(pageInfo.getTotal());

		return resultList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public TaotaoResult addItem(TbItem item, String desc) {
		// 生成商品ID
		long itemId = IDUtils.genItemId();
		// 补全item的属性
		item.setId(itemId);
		// 商品状态，1-正常，2-下架，3-删除
		item.setStatus(((byte) 1));
		item.setCreated(new Date());
		item.setUpdated(new Date());
		this.itemMapper.insert(item);

		// 添加商品描述
		TbItemDesc tbItemDesc = new TbItemDesc();
		tbItemDesc.setItemId(itemId);
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setCreated(new Date());
		tbItemDesc.setUpdated(new Date());
		this.itemDescMapper.insert(tbItemDesc);

		jmsTemplate.send(this.destination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				// 发送商品id
				TextMessage textMessage = session.createTextMessage(itemId + "");
				return textMessage;
			}
		});

		return TaotaoResult.ok();
	}

	@Override
	public TbItemDesc getTbItemDescById(Long itemId) {
		// 先查询缓存
		try {
			String json = jedisClient.get(ITEM_INFO + ":" + itemId + ":DESC");
			if (!StringUtils.isBlank(json)) {
				// 把json转换成对象
				return JsonUtils.jsonToPojo(json, TbItemDesc.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//查询数据库
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);

		try {
			// 把查询结果添加到缓存
			jedisClient.set(ITEM_INFO + ":" + itemId + ":DESC", JsonUtils.objectToJson(itemDesc));
			// 设置过期时间，提高缓存的利用率
			jedisClient.expire(ITEM_INFO + ":" + itemId + ":DESC", ITEM_EXPIRE);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return itemDesc;
	}
	
	

}
