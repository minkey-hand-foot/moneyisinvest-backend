import React from "react";
/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";
import {BiErrorCircle} from 'react-icons/bi';

export default function Message(props) {
    const message = css`
    display: ${props.state ? "block" : "none"};
    color: #FF1C1C;
    font-size: 0.75rem;
    font-weight: 500;
    display: flex;
    align-items: center;
    margin-bottom: 1.25rem;
    gap: 0.25rem;
`
    return (
        <div css={message}>
            <BiErrorCircle size="1.5rem" color="#FF1C1C" display={props.state ? "block" : "none"} margin="0"/>
            {props.text}
        </div>
    )
}