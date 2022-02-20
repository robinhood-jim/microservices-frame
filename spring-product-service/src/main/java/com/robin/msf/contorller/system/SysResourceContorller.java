package com.robin.msf.contorller.system;

import com.robin.basis.model.system.SysResource;
import com.robin.basis.service.system.SysResourceService;
import com.robin.basis.service.system.SysRoleService;
import com.robin.basis.vo.LoginUserVO;
import com.robin.core.base.exception.ServiceException;
import com.robin.core.collection.util.CollectionMapConvert;
import com.robin.core.convert.util.ConvertUtil;
import com.robin.core.query.util.PageQuery;
import com.robin.core.web.controller.AbstractCrudController;
import com.robin.msf.comm.utils.SecurityContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/system/menu")
@Api(value = "菜单资源管理", tags = {"菜单管理"}, description = "仅管理员有权操作此菜单")
public class SysResourceContorller extends AbstractCrudController<SysResource, Long, SysResourceService> {

    @Autowired
    private SysRoleService sysRoleService;

    @GetMapping("/nav")
    @ResponseBody
    public Map<String,Object> nav(){
        List<Map<String, Object>> list = service.queryBySql("select id,res_name as name,url,pid from t_sys_resource_info where RES_TYPE=? and status=?  ORDER BY RES_CODE,PID,SEQ_NO","1","1");
        Assert.isTrue(!CollectionUtils.isEmpty(list),"");
        //pid map
        Map<String,List<Map<String,Object>>> map=list.stream().collect(Collectors.groupingBy(f->f.get("pid").toString()));


        return null;


    }

    @ApiOperation("用户菜单列表")
    @GetMapping("/list")
    @ResponseBody
    public List<Map<String, Object>> list() {
        List<Map<String, Object>> list = service.queryBySql("select id,res_name as name,url,pid from t_sys_resource_info where RES_TYPE='1' ORDER BY RES_CODE,PID,SEQ_NO");
        List<Map<String, Object>> retList = new ArrayList<>();
        Map<String, Object> rmap = new HashMap<>();
        LoginUserVO vo= SecurityContextUtils.getLoginUserVO();
        Map<String,Object> userRights=service.getUserRights(vo.getUserId());
        List<Long> avaiableTops=new ArrayList<>();
        Map<Long,Map<String,Object>> sysMenusMap=new HashMap<>();
        try {
            if (!CollectionUtils.isEmpty(list)) {
                // pid and id List
                Map<String, List<Long>> map = CollectionMapConvert.getValuesByParentKey(list, "pid", "id", Long.class);
                Map<Long,Map<String,Object>> idMap=list.stream().collect(Collectors.toMap(f->Long.valueOf(f.get("id").toString()),f->f));
                List<Long> topNodes=map.get("0");
                if(!CollectionUtils.isEmpty(userRights)){
                    List<Map<String,Object>> permission=(List<Map<String,Object>>)userRights.get("permission");
                    permission.forEach(f->{
                        Long id=Long.valueOf(f.get("id").toString());
                        if(topNodes.contains(id)){
                            avaiableTops.add(id);
                            sysMenusMap.put(id,idMap.get(id));
                        }else{
                            sysMenusMap.put(id,idMap.get(id));
                            Long pid=Long.valueOf(f.get("pid").toString());
                            if(sysMenusMap.get(pid).containsKey("list")){
                                ((List<Map<String,Object>>)(sysMenusMap.get("pid").get("list"))).add(sysMenusMap.get(id));
                            }else{
                                List<Map<String,Object>> subList=new ArrayList<>();
                                subList.add(sysMenusMap.get(id));
                                sysMenusMap.get(pid).put("list",subList);
                            }
                        }
                    });
                }

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        avaiableTops.forEach(f->{
            retList.add(sysMenusMap.get(f));
        });
        return retList;
    }


    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> saveMenu(HttpServletRequest request,
                                        HttpServletResponse response) {
        Map<String, Object> retmap = new HashMap<String, Object>();
        try {
            Map<String, String> map = wrapRequest(request);
            SysResource resource = new SysResource();
            ConvertUtil.convertToModel(resource, map);
            resource.setType("1");
            Long id = service.saveEntity(resource);
            retmap.put("id", String.valueOf(id));
            retmap.put("success", "true");
            retmap.put("menu", resource);
        } catch (Exception ex) {
            ex.printStackTrace();
            retmap.put("success", "false");
            retmap.put("message", ex.getMessage());
        }
        return retmap;
    }

    @GetMapping("/showrole")
    public Map<String, Object> showAssignRole(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> retMap = new HashMap<>();
        String id = request.getParameter("id");
        List<Map<String, Object>> list = service.queryBySql("select id,role_name as name from t_sys_role_info where id in(select role_id from t_sys_resource_role_r where res_id=?)", new Object[]{new Integer(id)});
        List<Map<String, Object>> list1 = service.queryBySql("select id,role_name as name from t_sys_role_info where id not in(select role_id from t_sys_resource_role_r where res_id=?)", new Object[]{new Integer(id)});
        retMap.put("roleList", list);
        retMap.put("avaliableList", list1);
        retMap.put("resId", id);
        return retMap;
    }

    @GetMapping("/edit/{id}")
    @ResponseBody
    public Map<String, Object> queryUser(@PathVariable Long id) {
        return doEdit(id);
    }

    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> updateSysResource(HttpServletRequest request,
                                                 HttpServletResponse response) {
        Map<String, Object> retmap = new HashMap<String, Object>();
        try {
            Map<String, String> map = wrapRequest(request);
            Long id = Long.valueOf(request.getParameter("id"));
            SysResource user = service.getEntity(id);
            SysResource tmpuser = new SysResource();
            ConvertUtil.convertToModel(tmpuser, map);
            ConvertUtil.convertToModelForUpdate(user, tmpuser);
            service.updateEntity(user);
            retmap.put("id", String.valueOf(id));
            retmap.put("success", "true");
        } catch (Exception ex) {
            ex.printStackTrace();
            retmap.put("success", "false");
            retmap.put("message", ex.getMessage());
        }

        return retmap;
    }

    @PostMapping("/assignrole")
    @ResponseBody
    public Map<String, Object> assignRole(HttpServletRequest request) {
        String[] ids = request.getParameter("selRoleIds").split(",");
        Map<String, Object> retmap = new HashMap<String, Object>();
        try {
            sysRoleService.saveRoleRigth(ids, request.getParameter("resId"));

            retmap.put("success", "true");
        } catch (ServiceException ex) {
            ex.printStackTrace();
            retmap.put("success", "false");
        }
        return retmap;
    }

    @Override
    protected String wrapQuery(HttpServletRequest httpServletRequest, PageQuery pageQuery) {
        return null;
    }
}
