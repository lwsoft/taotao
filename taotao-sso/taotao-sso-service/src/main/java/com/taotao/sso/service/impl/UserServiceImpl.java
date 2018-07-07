package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserService;

/**
 * 用户处理service
 * 
 * @author
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	@Autowired    
	private JedisClient jedisClient;
	
	@Value("${USER_SESSION}")
	private String USER_SESSION;
	@Value("${SESSION_EXPIRE}")
	private Integer SESSION_EXPIRE;

	
	@Override
	public TaotaoResult checkData(String data, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		if (type == 1) {// 判断用户名是否可用
			criteria.andUsernameEqualTo(data);
		} else if (type == 2) {// 2.判断电话是否可用
			criteria.andPhoneEqualTo(data);
		} else if (type == 3) {// 3.判断邮箱是否可用
			criteria.andEmailEqualTo(data);
		} else {
			return TaotaoResult.build(400, "参数中包含非法数据");
		}

		List<TbUser> list = userMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			return TaotaoResult.ok(false);
		}

		return TaotaoResult.ok(true);

	}

	@Override
	public TaotaoResult register(TbUser user) {
		//检查数据有效性
		
		if(StringUtils.isBlank(user.getUsername())){
			return TaotaoResult.build(400, "用户名不能为空！");  
			
		}
		//判断用户名不能为空
		TaotaoResult taoResult = this.checkData(user.getUsername(),1);
		if(!(boolean)taoResult.getData()){
			return TaotaoResult.build(400, "用户名不能重复！");
		}
		//判断密码不能为空
		if(StringUtils.isBlank(user.getPassword())){
			return TaotaoResult.build(400, "密码不能为空！");
		}
		
		if(StringUtils.isNotBlank(user.getPhone())){
			//如果电话不为空，那么接着判断是否重复，电话是不能重复的 
			taoResult = this.checkData(user.getPhone(),2);
			if(!(boolean)taoResult.getData()){
				return TaotaoResult.build(400, "电话不能重复！");
			}
		}
		if(StringUtils.isNotBlank(user.getEmail())){
			//如果邮箱不为空，那么接着判断是否重复，邮箱也是不能重复的 
			taoResult = this.checkData(user.getEmail(),3);
			if(!(boolean)taoResult.getData()){
				return TaotaoResult.build(400, "邮箱不能重复！");
			}
		}
		
		//补充pojo属性
		
		user.setCreated(new Date());
		user.setUpdated(new Date());
		//密码要进行Md5加密，我们不用添加额外的jar包，只需要使用Spring自带的包就可以了
		String md5Pass = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5Pass);
		//插入数据
		this.userMapper.insert(user);
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult login(String username, String password) {
		//判断用户名和密码是否正确
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		//密码要进行MD5加密校验
		criteria.andPasswordEqualTo( DigestUtils.md5DigestAsHex(password.getBytes()));
		List<TbUser> list = userMapper.selectByExample(example);
		if(null == list ||list.size() ==0){
			//返回登录失败
			return TaotaoResult.build(400, "用户或密码不正确");
		}
		TbUser user = list.get(0);
		//生成token，使用uuid
		String token = UUID.randomUUID().toString();
		//清空密码
		user.setPassword(null);
		//把用户信息保存到redis，key是uuid，value是用户信息
		this.jedisClient.set(USER_SESSION + ":" + token, JsonUtils.objectToJson(user));
		
		//设置key的过期时间
		jedisClient.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
		//返回登录成功，其中要把token返回。
		
		return TaotaoResult.ok(token);
	}

	@Override
	public TaotaoResult getUserByToken(String token) {
		//
		String json = jedisClient.get(USER_SESSION + ":" + token);
		if(StringUtils.isBlank(json)){
			return TaotaoResult.build(400, "用户登录已过期");
		}
		//重置session过期时间
		jedisClient.expire(USER_SESSION + ":" + token, SESSION_EXPIRE);
		//把json转成user对象
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		return TaotaoResult.ok(user);
	}

	@Override
	public TaotaoResult logout(String token) {
		jedisClient.expire(USER_SESSION + ":" + token, 0);
		return TaotaoResult.ok();
	}

}
