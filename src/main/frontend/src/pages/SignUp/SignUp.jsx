import React, {useState, useRef} from "react";
import axios from "axios";
import "./SignUp.scss";
import Header from "../../systems/Header";
import Button from "components/Button";
import Footer from "components/Footer";
import Message from "components/Message";
import { useNavigate } from "react-router-dom";


export default function SignIn() {
    const [name, setName] = useState("");
    const [id, setId] = useState("");
    const [pw, setPw] = useState("");
    const [rePw, setRePw] = useState("");
    const [nameMessage, setNameMessage] = useState("");
    const [isName, setIsName] = useState(false);
    const [idMessage, setIdMessage] = useState("");
    const [isId, setIsId] = useState(false);
    const [pwMessage, setPwMessage] = useState("");
    const [isPw, setIsPw] = useState(false);
    const [rePwMessage, setRePwMessage] = useState("");
    const [isrePw, setIsRePw] = useState(false);
    const [isConfirm, setIsConfirm] = useState(false);

    const navigate = useNavigate();

    const handleInputName = (e) => {
        setName(e.target.value);
    }
    const handleInputId = (e) => {
        setId(e.target.value);
    }
    const handleInputPw = (e) => {
        setPw(e.target.value);
    }
    const handleInputRePw = (e) => {
        setRePw(e.target.value);
    }

    const nameRef = useRef("");
    const idRef = useRef("");
    const pwRef = useRef("");
    const rePwRef = useRef("");


    const onClickSignUp = () => {
        if (nameRef.current.value === '') {
            nameRef.current.focus();
            setIsName(true);
            setNameMessage("이름이 입력되지 않았습니다.");
            setIsId(false);
            setIdMessage("");
            setIsPw(false);
            setPwMessage("");
            setIsRePw(false);
            setRePwMessage("");
        }
        else if (idRef.current.value === '') {
            idRef.current.focus();
            setIsName(false);
            setNameMessage("");
            setIsId(true);
            setIdMessage("아이디가 입력되지 않았습니다!");
            setIsPw(false);
            setPwMessage("");
            setIsRePw(false);
            setRePwMessage("");
        }
        else if (!isConfirm) {
            idRef.current.focus();
            setIsName(false);
            setNameMessage("");
            setIsId(true);
            setIdMessage("아이디가 인증되지 않았습니다!");
            setIsPw(false);
            setPwMessage("");
            setIsRePw(false);
            setRePwMessage("");
        }
        else if (pwRef.current.value === '') {
            pwRef.current.focus();
            setIsName(false);
            setNameMessage("");
            setIsId(false);
            setIdMessage("");
            setIsPw(true);
            setPwMessage("비밀번호가 입력되지 않았습니다.");
            setIsRePw(false);
            setRePwMessage("");
        }
        else if (rePwRef.current.value === '') {
            rePwRef.current.focus();
            setIsName(false);
            setNameMessage("");
            setIsId(false);
            setIdMessage("");
            setIsPw(false);
            setPwMessage("");
            setIsRePw(true);
            setRePwMessage("비밀번호를 다시 한 번 입력해주세요!");
        }
        else if (pwRef.current.value !== rePwRef.current.value) {
            rePwRef.current.focus();
            setIsName(false);
            setNameMessage("");
            setIsId(false);
            setIdMessage("");
            setIsPw(false);
            setPwMessage("");
            setIsRePw(true);
            setRePwMessage("비밀번호를 다시 확인해주세요!");
        }
        else {
        sessionStorage.removeItem('rememberId');
        axios
            .post("/api/v1/sign-up", {
                name: name,
                password: pw,
                uid: id
              }).then((res)=> {
                console.log("!!", res.data);
                if (res.data.success === false) {
                    if (res.data.msg === "이미 가입된 회원") {
                        setIsName(false);
                        setNameMessage("");
                        setIsId(false);
                        setIdMessage("");
                        setIsPw(false);
                        setPwMessage("");
                        setIsRePw(false);
                        setRePwMessage("");
                        setId("");
                        setName("");
                        setPw("");
                        setRePw("");
                        alert("이미 가입된 회원입니다!");
                        return;
                    }
                    else {
                        alert("회원가입을 다시 시도해주세요!");
                    }
                }
                else {
                    navigate("/signIn", {replace: true});
                }
              }).catch((error) => {
                alert("에러가 발생했어요! 관리자에게 문의해주세요.");
                console.log(error);
              })
        }
    }

    const onClickConfirm = () => {
        const EMAIL_REGEX = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
        if (sessionStorage.getItem('rememberId') !== idRef.current.value) {
            if (idRef.current.value === '') {
                idRef.current.focus();
                setIsName(false);
                setNameMessage("");
                setIsId(true);
                setIdMessage("아이디가 입력되지 않았습니다.");
                setIsPw(false);
                setPwMessage("");
                setIsRePw(false);
                setRePwMessage("");
            }
            else if (!id.match(EMAIL_REGEX)) {
                setIsName(false);
                setNameMessage("");
                setIsId(true);
                setIdMessage("정확한 이메일을 입력해주세요!");
                setIsPw(false);
                setPwMessage("");
                setIsRePw(false);
                setRePwMessage("");
            }
            else {
                alert("인증이 완료되었습니다!");
                setIsName(false);
                setNameMessage("");
                setIsId(false);
                setIdMessage("");
                setIsPw(false);
                setPwMessage("");
                setIsRePw(false);
                setRePwMessage("");
                setIsConfirm(true);
                sessionStorage.setItem('rememberId', id);
            }
        }
        else {
            setIsName(false);
            setNameMessage("");
            setIsId(true);
            setIdMessage("이미 인증된 이메일입니다!");
            setIsPw(false);
            setPwMessage("");
            setIsRePw(false);
            setRePwMessage("");
        }
    }

    return (
        <div className="signupContainer">
            <Header/>
            <div className="signupBox">
                <div className="signupContent">
                    <div className="signupTitle">회원가입</div>
                    <div>
                        <label className="signupInput">이름<input type="text" value={name} onChange={handleInputName} ref={nameRef} /></label>
                    </div>
                    <Message text={nameMessage} state={isName} />
                    <div className="signConfirm">
                        <label className="signupInput">이메일<input type="text" value={id} onChange={handleInputId} ref={idRef}/></label>
                        <div onClick={onClickConfirm}>
                        <Button state="signConfirm" />
                        </div>
                    </div>
                    <Message text={idMessage} state={isId}/>
                    {/*<div>
                        <label className="signupInput">인증번호<input type="text" /></label>
                    </div>*/}
                    <div>
                        <label className="signupInput">비밀번호<input type="password" value={pw} onChange={handleInputPw} ref={pwRef}/></label>
                    </div>
                    <Message text={pwMessage} state={isPw}/>
                    <div>
                        <label className="signupInput">비밀번호 재입력<input type="password" value={rePw} onChange={handleInputRePw} ref={rePwRef}/></label>
                    </div>
                    <Message text={rePwMessage} state={isrePw}/>
                    <div className="signButton"  onClick={onClickSignUp} >
                        <Button type="submit" state="signup"/>
                    </div>
                </div>
                <Footer />
            </div>
        </div>
    )
}