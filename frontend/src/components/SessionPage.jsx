import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { Card, Form, Button, Alert, Container, Row, Col, ListGroup, Badge, Spinner } from 'react-bootstrap';
import { restaurantAPI, sessionAPI, userAPI, handleApiError } from '../services/api';

const SessionPage = () => {
    
    const { sessionCode } = useParams();
    const location = useLocation();
    const navigate = useNavigate();
    
    const [username, setUsername] = useState('');
    const [restaurantName, setRestaurantName] = useState('');
    const [restaurants, setRestaurants] = useState([]);
    const [randomRestaurant, setRandomRestaurant] = useState(null);
    const [sessionInfo, setSessionInfo] = useState(null);
    const [loading, setLoading] = useState({
        submit: false,
        random: false,
        fetch: false
    });
    const [alerts, setAlerts] = useState({
        error: '',
        success: ''
    });
    const [isLocked, setIsLocked] = useState(false);
    const [canRequestRandom, setCanRequestRandom] = useState(false);

    useEffect(() => {
        console.log('[SessionPage] useEffect triggered');
        // Get username from URL params
        const params = new URLSearchParams(location.search);
        const user = params.get('user');
        if (user) {
            setUsername(user);
            console.log(`[SessionPage] Username from URL: ${user}`);
        }
        
        // Fetch initial data
        fetchSessionData();
        
        // Check if session is locked
        checkSessionStatus();
        
        // Check if user can request random
        checkCanRequestRandom(user);
    }, [sessionCode, location.search]);

    const fetchSessionData = async () => {
        console.log(`[SessionPage] Fetching session data for: ${sessionCode}`);
        setLoading({...loading, fetch: true});
        try {
            // Get session info
            const sessionResponse = await sessionAPI.getSession(sessionCode);
            setSessionInfo(sessionResponse.data.data);
            console.log('[SessionPage] Session info loaded:', sessionResponse.data.data);
            
            // Get restaurants
            const restaurantsResponse = await restaurantAPI.getRestaurants(sessionCode);
            setRestaurants(restaurantsResponse.data.data);
            console.log(`[SessionPage] Restaurants loaded: ${restaurantsResponse.data.data.length}`);
        } catch (error) {
            showAlert('error', handleApiError(error));
        } finally {
            setLoading({...loading, fetch: false});
        }
    };

    const checkSessionStatus = async () => {
        console.log(`[SessionPage] Checking session status: ${sessionCode}`);
        try {
            const response = await sessionAPI.getSession(sessionCode);
            // Check if session is locked by checking if random restaurant has been selected
            const locked = response.data.data.locked || false;
            setIsLocked(locked);
            console.log(`[SessionPage] Session locked status: ${locked}`);
        } catch (error) {
            console.error('[SessionPage] Error checking session status:', error);
        }
    };

    const checkCanRequestRandom = async (user) => {
        console.log(`[SessionPage] Checking random request permission: ${sessionCode}, user: ${user}`);
        if (!user) return;
        try {
            const response = await restaurantAPI.canRequestRandom(sessionCode, user);
            const canRequest = response.data.data.canRequest;
            setCanRequestRandom(canRequest);
            console.log(`[SessionPage] Can request random: ${canRequest}`);
        } catch (error) {
            console.error('[SessionPage] Error checking random request permission:', error);
        }
    };

    const showAlert = (type, message) => {
        console.log(`[SessionPage] Showing ${type} alert: ${message}`);
        setAlerts({...alerts, [type]: message});
        setTimeout(() => {
            setAlerts({...alerts, [type]: ''});
        }, type === 'error' ? 5000 : 3000);
    };

    const handleSubmitRestaurant = async (e) => {
        console.log('[SessionPage] Submitting restaurant');
        e.preventDefault();
        
        if (!restaurantName.trim()) {
            showAlert('error', 'Please enter a restaurant name');
            return;
        }

        setLoading({...loading, submit: true});
        
        try {
            // Backend handles validation (session locked, user permissions, etc.)
            const response = await restaurantAPI.submitRestaurant(sessionCode, restaurantName, username);
            setRestaurants([...restaurants, response.data.data]);
            setRestaurantName('');
            checkCanRequestRandom(username);
            showAlert('success', 'Restaurant submitted successfully!');
        } catch (error) {
            showAlert('error', handleApiError(error));
        } finally {
            setLoading({...loading, submit: false});
        }
    };

    const handleGetRandomRestaurant = async () => {
        console.log('[SessionPage] Getting random restaurant');
        setLoading({...loading, random: true});
        
        try {
            // Backend handles all validation now (first submitter check, session state, etc.)
            const response = await restaurantAPI.getRandomRestaurant(sessionCode);
            setRandomRestaurant(response.data.data);
            setIsLocked(true);
            showAlert('success', 'Random restaurant selected!');
        } catch (error) {
            showAlert('error', handleApiError(error));
        } finally {
            setLoading({...loading, random: false});
        }
    };

    const handleKeyPress = (event, action) => {
        if (event.key === 'Enter') {
            action();
        }
    };

    const handleBackToLanding = () => {
        console.log('[SessionPage] Navigating back to landing page');
        navigate('/');
    };

    if (loading.fetch) {
        return (
            <Container className="text-center mt-5">
                <Spinner animation="border" />
                <p className="mt-3">Loading session...</p>
            </Container>
        );
    }

    return (
        <Container className="mt-5">
            <Row className="justify-content-center">
                <Col md={10}>
                    <Card className="shadow">
                        <Card.Body>
                            <div className="d-flex justify-content-between align-items-center mb-4">
                                <div>
                                    <Card.Title className="mb-1">Session: {sessionCode}</Card.Title>
                                    <Card.Subtitle className="text-muted">
                                        User: {username}
                                    </Card.Subtitle>
                                </div>
                                <Button variant="secondary" onClick={handleBackToLanding}>
                                    Back to Home
                                </Button>
                            </div>

                            {alerts.error && (
                                <Alert variant="danger" onClose={() => setAlerts({...alerts, error: ''})} dismissible>
                                    {alerts.error}
                                </Alert>
                            )}
                            
                            {alerts.success && (
                                <Alert variant="success" onClose={() => setAlerts({...alerts, success: ''})} dismissible>
                                    {alerts.success}
                                </Alert>
                            )}

                            {/* Random Restaurant Result */}
                            {randomRestaurant && (
                                <Card className="bg-success text-white mb-4">
                                    <Card.Body>
                                        <Card.Title>Your random choice is.... </Card.Title>
                                        <h2>{randomRestaurant.restaurantName}</h2>
                                        <p className="mb-0">Submitted by: {randomRestaurant.submittedBy}</p>
                                    </Card.Body>
                                </Card>
                            )}

                            {/* Restaurant Submission Form */}
                            <Card className="mb-4">
                                <Card.Body>
                                    <Card.Title>Submit Restaurant</Card.Title>
                                    <Form onSubmit={handleSubmitRestaurant}>
                                        <Row>
                                            <Col md={8}>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Restaurant Name</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        placeholder="Enter restaurant name"
                                                        value={restaurantName}
                                                        onChange={(e) => setRestaurantName(e.target.value)}
                                                        onKeyPress={(e) => handleKeyPress(e, handleSubmitRestaurant)}
                                                        disabled={loading.submit || isLocked}
                                                    />
                                                </Form.Group>
                                            </Col>
                                            <Col md={4}>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>&nbsp;</Form.Label>
                                                    <Button 
                                                        variant="primary" 
                                                        type="submit"
                                                        disabled={loading.submit || isLocked}
                                                        className="w-100"
                                                    >
                                                        {loading.submit ? 'Submitting...' : 'Submit Restaurant'}
                                                    </Button>
                                                </Form.Group>
                                            </Col>
                                        </Row>
                                    </Form>
                                </Card.Body>
                            </Card>

                            {/* Restaurant List */}
                            <Card>
                                <Card.Body>
                                    <div className="d-flex justify-content-between align-items-center mb-3">
                                        <Card.Title className="mb-0">Submitted Restaurants</Card.Title>
                                        <div>
                                            {isLocked ? (
                                                <Badge bg="danger">Session Locked</Badge>
                                            ) : (
                                                <Badge bg="success">Open</Badge>
                                            )}
                                        </div>
                                    </div>
                                    
                                    {restaurants.length === 0 ? (
                                        <p className="text-muted">No restaurants submitted yet.</p>
                                    ) : (
                                        <ListGroup variant="flush">
                                            {restaurants.map((restaurant, index) => (
                                                <ListGroup.Item key={restaurant.id || index} className="d-flex justify-content-between align-items-center">
                                                    <div>
                                                        <strong>{restaurant.restaurantName}</strong>
                                                        <br />
                                                        <small className="text-muted">Submitted by: {restaurant.submittedBy}</small>
                                                    </div>
                                                    {restaurant.submittedBy === username && (
                                                        <Badge bg="primary">You</Badge>
                                                    )}
                                                </ListGroup.Item>
                                            ))}
                                        </ListGroup>
                                    )}
                                </Card.Body>
                            </Card>

                            {/* Random Selection Button */}
                            <div className="text-center mt-4">
                                <Button 
                                    variant="danger" 
                                    size="lg"
                                    onClick={handleGetRandomRestaurant}
                                    disabled={loading.random || isLocked || restaurants.length === 0 || !canRequestRandom}
                                >
                                    {loading.random ? (
                                        <>
                                            <Spinner
                                                as="span"
                                                animation="border"
                                                size="sm"
                                                role="status"
                                                aria-hidden="true"
                                            />
                                            {' '} Selecting...
                                        </>
                                    ) : (
                                        'Get Random Restaurant'
                                    )}
                                </Button>
                                {!canRequestRandom && restaurants.length > 0 && (
                                    <div className="text-muted mt-2">
                                        Note: Only the first person to submit a restaurant can request a random choice.
                                    </div>
                                )}
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
};

export default SessionPage;