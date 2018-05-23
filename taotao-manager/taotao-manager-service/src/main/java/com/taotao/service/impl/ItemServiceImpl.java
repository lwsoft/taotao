package com.taotao.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;
import com.taotao.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService{

	@Autowired
	private TbItemMapper itemMapper;
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

}
