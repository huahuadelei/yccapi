import Vue from 'vue'
import VueRouter from 'vue-router'
import userStore from '@/utils/userStore';


Vue.use(VueRouter);

import Error404 from '@/pages/Error_404'
import Login from '@/pages/Login'
import Index from '@/pages/index/Index'
import ShowView from '@/pages/ShowView'
import ViewInfo from '@/pages/ViewInfo'
    import Home from '@/pages/index/subPages/Home'
    import MenuPermission from '@/pages/index/subPages/MenuPermisson'
    import RoleRule from '@/pages/index/subPages/RoleRule'
    import UserMember from '@/pages/index/subPages/UserMember'
    import Project from '@/pages/index/subPages/project/Project'
    import ApiList from '@/pages/index/subPages/project/ApiList'
    import ApiEdit from '@/pages/index/subPages/project/ApiEdit'
    import SysLog from '@/pages/index/subPages/SysLog'
    import EmailRule from '@/pages/index/subPages/EmailRule'
    import Views from '@/pages/index/subPages/Views'
    import DataType from '@/pages/index/subPages/DataType'


const vueRouter = new VueRouter({
    mode: "hash",
    routes: [
        {
            path: "/",
            redirect: "/show-view"

        },
        {
            name: "show-view",
            path: "/show-view",
            component: ShowView,
            meta: {
                title: "视图列表"
            }
        },
        {
            name: "view-info",
            path: "/view-info/:viewId",
            component: ViewInfo,
            meta: {
                title: "视图详情"
            }
        },
        {
            name: "index",
            path: "/index",
            component: Index,
            meta: {
                title: "首页"
            },
            children: [
                {
                    name: "home",
                    path: "/home",
                    component: Home,
                    meta: {
                        title: "欢迎页"
                    }
                },
                {
                    name: "menu-permission",
                    path: "/menu-permission",
                    component: MenuPermission,
                    meta: {
                        title: "菜单权限"
                    }
                },
                {
                    name: "role-rule",
                    path: "/role-rule",
                    component: RoleRule,
                    meta: {
                        title: "角色配置"
                    }
                },
                {
                    name: "projects",
                    path: "/projects",
                    component: Project,
                    meta: {
                        title: "项目管理"
                    }
                },
               
                {
                    name: "api-list",
                    path: "/api-list",
                    component: ApiList,
                    meta: {
                        title: "API列表",
                        keepAlive:true

                    }
                },
                {
                    name: "api-edit",
                    path: "/api-edit",
                    component: ApiEdit,
                    props:true,
                    meta: {
                        title: "API编辑"
                    }
                },
               
                {
                    name: "user-member",
                    path: "/user-member",
                    component: UserMember,
                    meta: {
                        title: "人员管理"
                    }
                },
                {
                    name: "logs",
                    path: "/logs",
                    component: SysLog,
                    meta: {
                        title: "日志管理"
                    }
                },
                {
                    name:"email-rule",
                    path:"/email-rule",
                    component: EmailRule,
                    meta: {
                        title: "邮箱配置"
                    }
                },
                {
                    name:"views",
                    path:"/views",
                    component: Views,
                    meta: {
                        title: "视图管理"
                    }
                },
                {
                    name:"data-type",
                    path:"/data-type",
                    component: DataType,
                    meta: {
                        title: "数据类型管理"
                    }
                }
            ]
        },
        {
            name: "login",
            path: "/login",
            component: Login,
            props:true,
            meta: {
                title: "登录页"
            }
        },
        {
            name: "error_404",
            path: "/*",
            component: Error404,
            props:true,
            meta: {
                title: "页面丢了"
            }
        }
    ]
});

const excludePages = ['show-view','view-info']

vueRouter.beforeEach((to, from, next) => {
    if (to.name != 'login'
        && !userStore.getToken()
        && excludePages.indexOf(to.name) == -1) {
        next({
            name: "login",
            params:{
                redirect:true
            }
        })
    } else {
        document.title = to.meta.title
        next()
    }

})

export default vueRouter;