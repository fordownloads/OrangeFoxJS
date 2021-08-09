            const style2CSS = (node, name) => {
                let normal = ''
                let hover = ''
                let src = ''
                let click = 'onclick="'
                let includeHover = false
                if (name !== undefined) {
                    normal = "\n."+name+"{\n"
                    hover = "\n."+name+":hover{\n"
                }
                let vis = true

                for (x of node)
                    if (vis)
                    switch (x.tagName) {
                        case 'font':
                            normal += "font: "+fonts[x.getAttribute('resource')]+";"
                            if (x.getAttribute('color') !== null) {
                                normal += "color: "+compute(x.getAttribute('color'))+";"
                            }
                            if (x.getAttribute('highlightcolor') !== null) {
                                hover += "color: "+compute(x.getAttribute('highlightcolor'))+";"
                                includeHover = true
                            }
                            break;
                        case 'fill':
                        case 'background':
                            normal += "background-color: "+compute(x.getAttribute('color'))+";"
                            break;
                        case 'highlight':
                            hover += "background-color: "+compute(x.getAttribute('color'))+";"
                            includeHover = true
                            break;
                        case 'image':
                            normal += "content: url('./images/"+images[x.getAttribute('resource')]+".png');"
                            normal += "background: url('./images/"+images[x.getAttribute('resource')]+".png') no-repeat;"
                            src += "src='./images/"+images[x.getAttribute('resource')]+".png' "
                            break;
                        case 'color':
                            if (x.getAttribute('foreground') !== null)
                                normal += "color: "+compute(x.getAttribute('foreground'))+";"
                            if (x.getAttribute('background') !== null)
                                normal += "background-color: "+compute(x.getAttribute('background'))+";"
                            break;
                        case 'placement':
                            let px = x.getAttribute('x')
                            let py = x.getAttribute('y')
                            let pw = x.getAttribute('w')
                            let ph = x.getAttribute('h')
                            let placement = x.getAttribute('placement')
                            if (px !== null) normal += `left:${compute(px)};`
                            if (py !== null) normal += `top:${compute(py)};`
                            if (pw !== null) normal += `width:${compute(pw)};`
                            if (ph !== null) normal += `height:${compute(ph)};`
                            switch (placement) {
                                case '1': normal += "transform: translateX(-100%);"; break;
                                case '2': normal += "transform: translateY(-100%);"; break;
                                case '3': normal += "transform: translate(-100%, -100%);"; break;
                                case '4': normal += "transform: translate(-50%, -50%);"; break;
                                case '5': normal += "transform: translateX(-50%);"; break;
                            }
                            break;
                        case 'action':
                            switch (x.getAttribute('function')) {
                                case 'set':
                                    click += "vars[`"+x.textContent.replace("=", "`]=`")+"`;"
                                    break;

                                case 'page':
                                    click += "setPage(`"+x.textContent+"`);"
                                    break;
                                case 'key':
                                    if (x.textContent == "home")
                                        click += "eval(homeEval);"
                                    else if (x.textContent == "back")
                                        click += "eval(backEval);"
                                    break;
                            }
                            break;
                        case 'condition':
                            vis = checkCond(x)
                            break;
                        case undefined: break;
                        default:
                            //console.warn(x.tagName+": CSS Unknown tag")
                            break;
                    }

                if (name === undefined) {
                    if (!vis) normal = "display: none"
                    if (click === 'onclick="') click = "";else {
                        if (click.includes('setPage('))
                            click+='"'
                        else
                            click+='setPage(lastpage)"'
                    }
                    if (includeHover)
                        return click + src + 'style="'+normal+'" data-css="'+normal+'" onmouseover="style=`'+hover+'` onmouseleave="style=dataset.css"'
                    else
                        return click + src + 'style="'+normal+'"'
                } else {
                    if (includeHover)
                        return normal+"\n}\n" + hover + "\n}\n"
                    else
                        return normal+"\n}\n"
                }
            }


            let homeEval = ""
            let backEval = ""

            const page2HTML = node => {
                homeEval = ""
                backEval = ""
                let ret = ""
                let touchMode = false
                let istrue = true
                for (x of node)
                    switch (x.tagName) {
                        case "background":
                            ret += "<div class='bg' style='background:"+compute(x.getAttribute('color'))+"'></div>"
                            break;
                        case "action":
                            touchMode = false
                            istrue = true
                            for (cond of x.getElementsByTagName("condition")) {
                                istrue = checkCond(cond)
                                if (istrue === false)
                                    break
                            }

                            if (istrue)
                            for (a of x.children) {
                                if (a.tagName === "action") {
                                    if (touchMode === "home")
                                        switch (a.getAttribute('function')) {
                                            case 'set':
                                                homeEval += "vars[`"+a.textContent.replace("=", "`]=`")+"`;"
                                                break;

                                            case 'page':
                                                homeEval += "setPage(`"+a.textContent+"`);"
                                                break;
                                        }
                                    else if (touchMode === "back")
                                        switch (a.getAttribute('function')) {
                                            case 'set':
                                                backEval += "vars[`"+a.textContent.replace("=", "`]=`")+"`;"
                                                break;

                                            case 'page':
                                                backEval += "setPage(`"+a.textContent+"`);"
                                                break;
                                        }
                                    else {
                                        switch (a.getAttribute('function')) {
                                            case 'set':
                                                let set = a.textContent.split('=')
                                                vars[set[0]] = set[1]
                                                break;

                                            case 'page':
                                                setTimeout(()=>
                                                setPage(a.textContent), 10)
                                                console.log(a.textContent)
                                                break;
                                            case 'checkbackupfolder':
                                                setTimeout(()=>
                                                setPage('restore_prep'), 10)
                                                break;
                                        }}
                                } else if (a.tagName === "touch") touchMode = a.getAttribute('key')
                            }
                            break;
                        case "gesture":
                        case "battery":
                            break;
                        case "button":
                            let txtnode2 = x.getElementsByTagName('text')[0]
                            let txt2 = txtnode2 == undefined ? "" : compute(txtnode2.textContent)
                            ret += `<div class='${x.getAttribute('style') ?? x.tagName}' ${style2CSS(x.children)}>${txt2}</div>`
                        case "image":
                            ret += `<img class='${x.getAttribute('style') ?? x.tagName}' ${style2CSS(x.children)}>`
                            break;
                        case "text":
                        case "checkbox":
                            let txtnode = x.getElementsByTagName('text')[0]
                            let txt = txtnode == undefined ? "" : compute(txtnode.textContent)
                            ret += `<div class='${x.getAttribute('style') ?? x.tagName}' ${style2CSS(x.children)}>${txt}</div>`
                            break;
                        case "fill":
                            let color = compute(x.getAttribute('color'))
                            ret += `<div ${style2CSS(x.children)}><color style="background:${color}"></color></div>`
                            break;

                        case "keyboard":
                        case "console":
                        case "progressbar":
                        case "partitionlist":
                        case "fileselector":
                        case "listbox":
                        case "input":
                        case "animation":
                        case "slider":
                        case "slidervalue":
                            ret += `<div class='${x.getAttribute('style') ?? x.tagName}' ${style2CSS(x.children)}>${x.tagName}</div>`
                            break;
                        case 'template':
                            ret += page2HTML(templates[x.getAttribute('name')])
                            break;
                        case undefined: break;
                        default:
                            console.warn(x.tagName+": Unknown tag")
                            break;
                    }

                    varlistel.innerHTML = ""
                for (const [k, v] of Object.entries(varlist)) {
                    let varctrl = document.createElement('var')
                    varlistel.append(varctrl)
                    varctrl.textContent = k + ": " + v
                    varctrl.onclick = () => changeVar(k)
                }
                return ret
            }

