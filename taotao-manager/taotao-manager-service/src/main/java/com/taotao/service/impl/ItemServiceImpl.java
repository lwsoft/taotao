package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.IDUtils;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService{

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbItemDescMapper itemDescMapper;
	
	@Override
	public TbItem getItemByid(Long itmeId) {
		// TODO Auto-generated method stub
		return this.itemMapper.selectByPrimaryKey(itmeId);
	}
	@Override
	public EasyUIDataGridResult getItemList(int page, int pageSize) {
		//设置分页信息
		PageHelper.startPage(page, pageSize);
		// 执行查询
		TbItemExample tbItemExample = new TbItemExample();  
		
		List<TbItem>  list = this.itemMapper.selectByExample(tbItemExample);
		//取查询结果
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);		 
		EasyUIDataGridResult resultList = new EasyUIDataGridResult();		
		resultList.setRows(list);
		resultList.setTotal(pageInfo.getTotal());
		
		return resultList;
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	public TaotaoResult addItem(TbItem item, String desc) {
		//生成商品ID 
		long itemId = IDUtils.genItemId();
		//补全item的属性  
		item.setId(itemId);
		//商品状态，1-正常，2-下架，3-删除  
		item.setStatus(((byte) 1));
		item.setCreated(new Date());
		item.setUpdated(new Date());
		this.itemMapper.insert(item);
		
		//添加商品描述  
		TbItemDesc tbItemDesc = new TbItemDesc();
		tbItemDesc.setItemId(itemId);
		tbItemDesc.setItemDesc(desc);
		tbItemDesc.setCreated(new Date());  
		tbItemDesc.setUpdated(new Date());  
		this.itemDescMapper.insert(tbItemDesc);
		return TaotaoResult.ok();
	}

}
