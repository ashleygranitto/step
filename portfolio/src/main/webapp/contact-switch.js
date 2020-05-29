var current = "business";

// Switch from the business tab to the personal tab 
function businessToPersonal() { 
    if(current==="business"){ 
        document.getElementById('business-body').style.display = "none";
        document.getElementById('personal-body').style.display = "flex";
    }
    current = "personal";
}

// Switch from the business tab to the personal tab 
function personalToBusiness() {  
    if(current==="personal"){ 
        document.getElementById('business-body').style.display = "flex";
        document.getElementById('personal-body').style.display = "none";
    }
    current = "business";
}