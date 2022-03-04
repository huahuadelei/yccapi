import Axios from 'axios'
import router from '@/router'
import userStore from '@/utils/userStore';

const baseURL = "/ycc-api-admin";
const axios = Axios.create({
    baseURL:baseURL,
    timeout:30000

})
axios.interceptors.request.use((config)=>{
    const {headers} = config;
    const token = userStore.getToken();
    if(token){
        headers.token=token;
    }
    return config;
},error=>{
   return Promise.reject(error)
});

axios.interceptors.response.use((resp)=>{
    if(resp.data.code == '401'||
        resp.data.code == '407'||
        resp.data.code == '408'){
        userStore.cleanStore();
        router.push({name:"login"})
    }
    return resp;
},error=>{
    return Promise.reject(error)
 });

const api = {
    multi(...server){
        return Axios.all(server);
    },
    user:{
        login(data){
            return axios.post("/user/login",data);
        },
        getUserInfo(){
            return axios.get("/user/getUserInfo");
        },
        queryUserList(queryEntity){
            return axios.post('/user/query',queryEntity)
        },
        createUser(userEntity){
            return axios.post('/user/create',userEntity);
        },
        updateUser(userEntity){
            return axios.put('/user/update',userEntity);
        },
        resetPass(userEntity){
            return axios.post('/user/resetPass',userEntity);
        },
        // 修改当前登陆人密码
        currentResetPass(entity){
            return axios.post("/user/current/resetPass",entity);
        },
        deleteUser(userId){
            return axios.delete('/user/'+userId);
        }
    },
    menu:{
        getTree(){
            return axios.get("/menu/tree");
        },
        saveMenu(entity){
            return axios.post("/menu",entity);
        },
        removeMenu(menuId){
            return axios.delete('/menu',{data:{id:menuId}});
        }
    },
    role:{
        getRoleList(){
            return axios.get('/role');
        },
        getRoleMenuPermsOptions(roleId){
            return axios.get('/role/perms/'+roleId);
        },
        updateSelectMenus(data){
            return axios.post('/role/perms/update',data);
        },
        saveRoleEntity(entity){
            return axios.post('/role',entity);
        },
        deleteRole(roleId){
            return axios.delete('/role/'+roleId);
        }
    },
    test:{
        invokeHttpMethod(reqData){
            return axios.post('/test/http/'+reqData.method,reqData);
        }
    },
    log:{
        getSysLogList(queryEntity){
            return axios.post('/logs/query',queryEntity);
        }
    },
    config:{
        getConfig(configKey){
            return axios.get('/config/'+configKey);
        },
        updateConfig(configEntity){
            return axios.put('/config/'+configEntity.configKey,configEntity);
        }
    },
    dataType:{
        getDataTypeList(){
            return axios.get('/data-type');
        },
        updateDataType(entity){
            return axios.post('/data-type',entity);
        },
        deleteDataType(dataTypeId){
            return axios.delete('/data-type/'+dataTypeId);
        }
    },
    project:{
        queryProjectPage(queryEntity){
            return axios.post('/project/query',queryEntity);
        },
        getProjectList(){
            return axios.get('/project');
        },
        getProjectVersionList(projectId){
            return axios.get('/project/versions?projectId='+projectId);
        },
        deleteProject(proId){
            return axios.delete('/project/'+proId);
        },
        updateProject(entity){
            return axios.put('/project',entity);
        },
        saveProject(entity){
            return axios.post('/project',entity);
        },
        getProjectById(projectId){
            return axios.get('/project/'+projectId);
        },
        //获取项目版本集合
        proVersionList(projectId){
            return axios.get('/project/versions/'+projectId);
        },
        deleteProjectVersion(versionId){
            return axios.delete(`/project/version/${versionId}`)
        },
        // 删除项目分组
        deleteProjectGroup(groupId){
            return axios.delete(`/project/group/${groupId}`)
        } 
        // 添加项目版本
        ,addProjectVersion(entity){
            return axios.post(`/project/version`,entity)
        },
        // 添加项目分组
        addProjectGroup(entity){
            return axios.post('/project/group',entity)
        }

    },
    api:{
        getApiPage(queryEntity){
            return axios.post('/api/query',queryEntity);
        },
        getById(apiId){
            return axios.get('/api/'+apiId);
        },
        getGroupListByVersionId(versionId){
            return axios.get('/api/groups/'+versionId);
        },
        getRequestMethods(){
            return axios.get('/api/requestMethods');
        },
        getRequestHeaderNames(){
            return axios.get('/api/headerNames');
0        },
        updateApiInfo(entity){
            return axios.post('/api',entity);
        },
        deleteApiInfo(apiId){
            return axios.delete('/api/'+apiId);
        }

    },
    views:{
        createViewData(entity){
            return axios.post('/views',entity);
        },
        queryViewDatas(queryEntity){
            return axios.post('/views/query',queryEntity);
        },
        viewCounter(){
            return axios.get('/views/counter');
        },
        exportViewExcel(viewId){
            return new Promise((resolve,reject)=>{
                const targetUrl = baseURL+"/views/export/excel?viewId="+viewId;
                resolve({target:targetUrl});
            });
        }

    },
    notice:{
        sendMail(mailEntity){
            return axios.post('/notice/sendMail',mailEntity);
        }
    }



}

export default api;