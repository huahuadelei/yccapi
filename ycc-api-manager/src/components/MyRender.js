export default {
    name:"my-render",
    props:{
        renderFn:{
            type:Function,
            required:true
        },
        scoped:{
            type:Object,
            required:true
        }
    },
    render(h) {
        return this.renderFn(h,this.scoped);
    },
}