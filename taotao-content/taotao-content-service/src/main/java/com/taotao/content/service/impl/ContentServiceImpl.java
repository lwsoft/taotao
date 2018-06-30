package com.taotao.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.content.service.ContentService;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;

@Service
public class ContentServiceImpl implements ContentService {
	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private JedisClient jedisClient;
	@Value("${INDEX_CONTENT}")
	private String INDEX_CONTENT;

	@Override
	public EasyUIDataGridResult getContentList(long categoryId, int page, int rows) {
		// 设置分页信息
		PageHelper.startPage(page, rows);
		// 执行查询
		TbContentExample example = new TbContentExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andCategoryIdEqualTo(categoryId);
		// 获取查询结果
		List<TbContent> list = contentMapper.selectByExample(example);
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setRows(list);
		result.setTotal(pageInfo.getTotal());
		// 返回结果
		return result;
	}

	@Override
	public TaotaoResult addContent(TbContent content) {
		content.setCreated(new Date());
		content.setUpdated(new Date());
		contentMapper.insert(content);
		//同步缓存
		//删除对应的缓存信息
		jedisClient.hdel(INDEX_CONTENT, content.getCategoryId().toString());
		return TaotaoResult.ok();
	}

	@Override
	public List<TbContent> getContentListByCid(long cid) {
		//先查询缓存
		try{
			//查询缓存
			String json = jedisClient.hget(INDEX_CONTENT,cid+"");
			//查询到结果，把json转换成list返回
			if(StringUtils.isNotBlank(json)){
				List<TbContent> list = JsonUtils.jsonToList(json, TbContent.class);
				return list;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//添加缓存不能影响正常业务逻辑  
		
		//缓存没有命中，需要查询数据库
		TbContentExample example = new TbContentExample();
		
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> list = contentMapper.selectByExample(example);
		//把结果添加到缓存
		try{
			jedisClient.hset(INDEX_CONTENT, String.valueOf(cid),JsonUtils.objectToJson(list));
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
		 
	}

}
