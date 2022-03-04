const USET_TOKEN = 'userToken';
const USER_INFO = 'userInfo';
const USER_AUTO_LOGIN="AUTO_LOGIN";

const utils = {
    storeToken(token, autoLogin) {
        localStorage.setItem(USER_AUTO_LOGIN,autoLogin);
        const auto = localStorage.getItem(USER_AUTO_LOGIN);
       
        if (auto == 'true') {
            localStorage.setItem(USET_TOKEN, token);
        } else {
            sessionStorage.setItem(USET_TOKEN, token)
        }
    },
    getToken() {
        var token=null;
        const auto = localStorage.getItem(USER_AUTO_LOGIN);
        if(auto == 'true'){
            token = localStorage.getItem(USET_TOKEN);
        }else{
            token = sessionStorage.getItem(USET_TOKEN);
        }

        return token;
    },
    storeUserInfo(info){
        const auto = localStorage.getItem(USER_AUTO_LOGIN);
        info = JSON.stringify(info);
        if (auto == 'true') {
            localStorage.setItem(USER_INFO, info);
        } else {
            sessionStorage.setItem(USER_INFO, info)
        }
    },
    getUserInfo(){
        var userInfo=null;
        const auto = localStorage.getItem(USER_AUTO_LOGIN);
        if (auto == 'true') {
            userInfo = localStorage.getItem(USER_INFO);
        } else {
            userInfo = sessionStorage.getItem(USER_INFO);
        }
        return userInfo?JSON.parse(userInfo):null;
    },
    cleanStore(){
        localStorage.removeItem(USER_AUTO_LOGIN);
        localStorage.removeItem(USER_INFO);
        localStorage.removeItem(USET_TOKEN);
        sessionStorage.removeItem(USER_INFO);
        sessionStorage.removeItem(USET_TOKEN)
    }
}

export default utils;