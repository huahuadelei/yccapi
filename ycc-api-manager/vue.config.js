module.exports = {
    publicPath:"/ycc-api-admin",
    outputDir:"build",
    assetsDir:"api-static",

    devServer:{
        proxy:{
            "/ycc-api-admin":{
                target:"http://localhost:8099"
            }
        }
    },

    css:{
        loaderOptions:{
            scss: {
                additionalData: `@import "@/assets/globalStyle.scss";`
            }
        }
    }
  
}   