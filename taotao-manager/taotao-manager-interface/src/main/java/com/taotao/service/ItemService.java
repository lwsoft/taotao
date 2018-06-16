package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemService {
	
	TbItem getItemById(Long id);
	
	EasyUIDataGridResult getItemList(int page,int rows);
	
	TaotaoResult addItem(TbItem item,String desc);
	
	TbItemDesc getTbItemDescById(Long itemId);

}
