package com.robin.msf.contorller.system;

import com.robin.core.base.exception.ServiceException;
import com.robin.core.convert.util.ConvertUtil;
import com.robin.core.web.controller.BaseCrudDhtmlxController;
import com.robin.example.model.system.SysResource;
import com.robin.example.service.system.SysResourceService;
import com.robin.example.service.system.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/menu")
public class SysResourceContorller extends BaseCrudDhtmlxController<SysResource, Long, SysResourceService> {

    @Autowired
    private SysRoleService sysRoleService;

    @RequestMapping("/list")
    @ResponseBody
    public Map<String, Object> list(HttpServletRequest request,
                                    HttpServletResponse response) {
        List<Map<String, Object>> list = service.queryBySql("select id,res_name as name,url,pid from t_sys_resource_info where RES_TYPE='1' ORDER BY RES_CODE,PID,SEQ_NO");
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
        Map<String, Object> rmap = new HashMap<String, Object>();
        for (Map<String, Object> map : list) {
            String pid = map.get("pid").toString();
            if ("0".equals(pid)) {
                Map<String, Object> tmap = new HashMap<String, Object>();
                tmap.put("id", map.get("id"));
                tmap.put("text", map.get("name"));
                rmap.put(map.get("id").toString(), tmap);
                retList.add(tmap);
            } else {
                if (rmap.containsKey(pid)) {
                    Map<String, Object> tmpmap = (Map<String, Object>) rmap.get(pid);
                    Map<String, Object> t2map = new HashMap<String, Object>();
                    t2map.put("id", map.get("id"));
                    t2map.put("text", map.get("name"));
                    List<Map<String, String>> userdataList = new ArrayList<Map<String, String>>();
                    Map<String, String> usermap = new HashMap<String, String>();
                    usermap.put("name", "url");
                    usermap.put("value", map.get("url").toString());
                    userdataList.add(usermap);
                    t2map.put("userdata", userdataList);

                    if (!tmpmap.containsKey("item")) {
                        List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
                        list1.add(t2map);
                        tmpmap.put("item", list1);
                    } else {
                        List<Map<String, Object>> list1 = (List<Map<String, Object>>) tmpmap.get("item");
                        list1.add(t2map);
                    }
                }
            }
        }
        Map<String, Object> retMaps = new HashMap<String, Object>();
        retMaps.put("id", "0");
        retMaps.put("text", "菜单");
        retMaps.put("item", retList);
        return retMaps;
    }

    @RequestMapping("/save")
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

    @RequestMapping("/showrole")
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

    @RequestMapping("/edit")
    @ResponseBody
    public Map<String, Object> queryUser(HttpServletRequest request,
                                         HttpServletResponse response) {
        String id = request.getParameter("id");
        return doEdit(request, response, Long.valueOf(id));
    }

    @RequestMapping("/update")
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

    @RequestMapping("/assignrole")
    @ResponseBody
    public Map<String, Object> assignRole(HttpServletRequest request, HttpServletResponse response) {
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
}
