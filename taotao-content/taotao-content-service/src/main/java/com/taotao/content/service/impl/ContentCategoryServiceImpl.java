package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.EasyUITreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;

	@Override
	public List<EasyUITreeNode> getContentCategoryList(long parentId) {
		// 创建一个查询类
		TbContentCategoryExample contentCategoryExample = new TbContentCategoryExample();
		// 设置查询条件
		Criteria criteria = contentCategoryExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		// 查询
		List<TbContentCategory> categoryList = contentCategoryMapper.selectByExample(contentCategoryExample);
		// 将categoryList转换为List<EasyUITreeNode>
		List<EasyUITreeNode> resultList = new ArrayList<>();
		for (TbContentCategory contentCategory : categoryList) {
			EasyUITreeNode easyUITreeNode = new EasyUITreeNode();
			easyUITreeNode.setId(contentCategory.getId());
			easyUITreeNode.setText(contentCategory.getName());
			easyUITreeNode.setState(contentCategory.getIsParent() ? "closed" : "open");
			resultList.add(easyUITreeNode);
		}
		return resultList;

	}

	@Override
	public TaotaoResult addContentCategory(long parentId, String name) {
		// 实例化一个对象
		TbContentCategory contentCategory = new TbContentCategory();
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		// 状态。可选值:1(正常),2(删除)，刚添加的节点肯定是正常的
		contentCategory.setStatus(1);
		contentCategory.setSortOrder(1);
		// 刚添加的节点肯定不是父节点
		contentCategory.setIsParent(false);
		// 保存当前操作时间
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//插入节点到数据库 
		contentCategoryMapper.insert(contentCategory);
		//判断父节点的状态
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()){
			//如果父节点为叶子节点应该改为父节点
			parent.setIsParent(true);
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		return TaotaoResult.ok(contentCategory);
	}

}
