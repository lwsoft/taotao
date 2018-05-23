package com.taotao.pagehelper;

import java.util.List;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;

public class TestPageHelper {
	
	@Test
	public void testPageHelper() throws Exception{
		PageHelper.startPage(1,10);
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
		
		TbItemMapper itemMapper = (TbItemMapper)ctx.getBean(TbItemMapper.class);
		TbItemExample tbItemExample = new TbItemExample();  
		List<TbItem> list = itemMapper.selectByExample(tbItemExample);
		
		PageInfo<TbItem> pageInfo = new PageInfo<TbItem>(list);
		System.out.println(pageInfo.getTotal());
		System.out.println(list.size());
	}

}
