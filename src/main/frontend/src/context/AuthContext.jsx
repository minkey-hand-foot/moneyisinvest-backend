import {createContext, useState, useEffect} from 'react';

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [token, setToken] = useState(null);
    const [userId, setUserId] = useState(null);
    const [userName, setUserName] = useState(null);
    const [userProfile, setUserProfile] = useState(null);

    useEffect(() => {
        const storedToken = sessionStorage.getItem('token');
        const storedUserId = sessionStorage.getItem('id');
        const storedUserName = sessionStorage.getItem('name');
        const storedUserProfile = sessionStorage.getItem('profileImg');

        if(storedToken) {
            setToken(storedToken);
            setIsLoggedIn(true);
        }
        if (storedUserId) {
            setUserId(storedUserId);
          }
      
        if (storedUserName) {
            setUserName(storedUserName);
        }

        if (storedUserProfile) {
            setUserProfile(storedUserProfile);
        }
    }, []);

    const login = (newToken, newUserId, newUserName, newUserProfile) => {
        sessionStorage.setItem("token", newToken);
        sessionStorage.setItem("id", newUserId);
        sessionStorage.setItem("name", newUserName);
        sessionStorage.setItem("profileImg", newUserProfile);
        setToken(newToken);
        setUserId(newUserId);
        setUserName(newUserName);
        setUserProfile(newUserProfile);
        setIsLoggedIn(true);
    };

    const logout = () => {
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('id');
        sessionStorage.removeItem('name');
        sessionStorage.removeItem('profileImg');
        sessionStorage.clear();
        setToken(null);
        setUserId(null);
        setUserName(null);
        setUserProfile(null);
        setIsLoggedIn(false);
    };

    const updateProfile = (newUserName, newUserProfile) => {
        sessionStorage.setItem("name", newUserName);
        sessionStorage.setItem("profileImg", newUserProfile);
        setUserName(newUserName);
        setUserProfile(newUserProfile);
    }

    return(
        <AuthContext.Provider value={{isLoggedIn, token, userId, userName, userProfile, login, logout, updateProfile}}>
            {children}
        </AuthContext.Provider>
    );
};

export { AuthContext, AuthProvider };