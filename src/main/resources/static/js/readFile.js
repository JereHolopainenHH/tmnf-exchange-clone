document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.getElementById('file');
    const fileInfo = document.getElementById('file-info');

    fileInput.addEventListener('change', function(event) {
        const file = event.target.files[0];
        if(file){
            console.log(file);
        }

        // read file information and parse it
        const reader = new FileReader();
        reader.onload = function(e) {
            const content = e.target.result;

            // parse the content 
            const extractedInfo = extractInfroFromFileContent(content);
            console.log(extractedInfo);
            fileInfo.innerHTML = Object.keys(extractedInfo).map(key => `<p>${key}: ${extractedInfo[key]}</p>` ).join("");
        };

        reader.readAsText(file);
    });
    
    function extractInfroFromFileContent(content){
        //type="challenge" version="TMc.6" exever="2.11.26"><ident uid="CqLrkNWMKw7BhHWPZJ_JCKUP7Xc" name="Pipe Challenge for Gamers" author="jereasdxd"/><desc envir="Stadium" mood="Day" type="Race" nblaps="0" price="7639" /><times bronze="-1" silver="-1" gold="-1" authortime="-1" authorscore="-1"/><deps><dep file="Skins\Any\Advertisement\Advert2.zip"/><dep file="Skins\Any\Advertisement\SignDown.bik"/><dep file="Skins\Any\Advertisement\Advert1.zip"/><dep file="Skins\Any\Advertisement\SignRight.bik"/><dep file="Skins\Any\Advertisement\Advert3.zip"/><dep file="Skins\Any\Advertisement\SignLeft.bik"/></deps></header>

        const trackNamePattern = /name="([^"]+)"/;
        const authorPattern = /author="([^"]+)"/;
        const envirPattern = /envir="([^"]+)"/;
        const uidPattern = /uid="([^"]+)"/;
        const moodPattern = /mood="([^"]+)"/;
        return {
            uid: content.match(uidPattern)[1],
            author: content.match(authorPattern)[1],
            name: content.match(trackNamePattern)[1],
            envir: content.match(envirPattern)[1],
            mood: content.match(moodPattern)[1],
        }
    }

});