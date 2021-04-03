import React, { useEffect, useState } from "react";
import { vioalionAPI } from '../api/API';

function Violation() {
    const [vioaltionData, setVioaltionData] = useState({})
    const [vioaltionTile, setVioaltionTile] = useState('')

    useEffect(() => {
        violationAPI();

    }, []);

    const violationAPI = () => {
        fetch(vioalionAPI('SonarQubeOpenViolationMonitor', '2021-03'))
            .then(resp => resp.json())
            .then(data => setVioaltionData(data))
    };
    console.log({ vioaltionData })

    const setA = () => {
        console.log("object lenth : " + Object.keys(vioaltionData).length);
        if (Object.keys(vioaltionData).length !== 0) {

            console.log("vioaltionData.object :" + vioaltionData)
            const map = new Map(Object.entries(vioaltionData));
            console.log("map " + map)
            for(var a in map){
                console.log("pront map "+a)
            }
            // Object.keys(vioaltionData.object).map((key, i) => (
            //     <p key={i}>
            //         <span>Key Name: {key}</span>
            //         <span>Value: {vioaltionData.object[key]}</span>
            //     </p>
            // ))

        }
    }

    return (
        <div>
            {setA()
            }
        </div>

    );
}
export default Violation;