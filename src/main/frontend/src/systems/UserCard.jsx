// TopCard.jsx (새로운 파일로 분리한 컴포넌트)

import React from "react";
import { useScrollFadeIn } from "../hooks/useScrollFadeIn";
import Card from "systems/Card";

const UserCard = ({ item, index }) => {

    return (
        <div className="animated-card" key={index} {...useScrollFadeIn('left', 1, (index + 1) * 0.2)}>
            <Card 
                isVisible={false}
                company={item.company}
                code={item.code}
                rate={item.rate}
                price={item.price}
                stock={item.value}
            />
        </div>
    );
};

export default UserCard;
