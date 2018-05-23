package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TbItem;

public interface ItemService {
	
	TbItem getItemByid(Long id);
	
	EasyUIDataGridResult getItemList(int page,int rows);

}
