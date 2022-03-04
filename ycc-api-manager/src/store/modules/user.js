module.exports={
    state:{
        loginUser:{menuNodes:[],user:{}}
    },
    getters:{
        getLoginUser(state){
            return state.loginUser;
        }
    },
    mutations:{
        setLoginUser(state,loginUser){
            state.loginUser=loginUser
        }
    }
}