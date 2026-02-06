import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import LandingPage from './components/LandingPage';
import SessionPage from './components/SessionPage';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/session/:sessionCode" element={<SessionPage />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
