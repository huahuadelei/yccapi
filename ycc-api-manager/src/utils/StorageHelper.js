
const Store = function(instance){
    this.storeInstance = instance;

    this.setItem=function(key,value){
        this.storeInstance.setItem(key,JSON.stringify(value));
    }
    this.addItem=function(key,value){
        var v = this.getItem(key);
        if(!v){
           this.setItem(key,value); 
           return ;
        }

        if(!Array.isArray(v)){
             v = [v]
        }
        v.push(value);
        this.setItem(key,v);

    }
    this.getItem=function(key){
        var result = this.storeInstance.getItem(key);
        return result?JSON.parse(result):result;
    }
}

export default {

    session:{
        ... new Store(sessionStorage)
    },
    local:{
        ... new Store(localStorage)
    }
    
}
