import React from 'react';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Welcome from './component/Welcome';
import Dashboard from './component/Dashboard';
import SensorPage from './component/SensorPage';

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Welcome/>}/>
                <Route path="/dashboard" element={<Dashboard/>}/>
                <Route path="/dashboard/:sensorId" element={<SensorPage/>}/>
            </Routes>
        </Router>
    );
}

export default App;