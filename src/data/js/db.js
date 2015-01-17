function DB() {
    this.getRules = function() {
        return JSON.parse(localStorage.getItem("rules"));  
    }
    this.setRules = function(newRules) {
        localStorage.setItem("rules", JSON.stringify(newRules));
    }
    this.getIsRedirect = function() {
        return JSON.parse(localStorage.getItem("isRedirect")); 
    }
    this.setIsRedirect = function(isRedirect) {
        localStorage.setItem("isRedirect", isRedirect); 
    }
}
