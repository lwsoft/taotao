package com.taotao.search.mapper;

import java.util.List;

import com.taotao.common.pojo.SearchItem;

public interface SearchItemMapper {
	
	
	List<SearchItem> getSearchItemList();
	
	SearchItem getItemById(long itemId);

}
