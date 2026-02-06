import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {Card, Form, Button, InputGroup, Alert} from 'react-bootstrap';
import {sessionAPI, userAPI, handleApiError} from '../services/api';

const LandingPage = () => {

    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        initiatorUsername: '',
        joinSessionCode: '',
        joinUsername: ''
    });

    const [loading, setLoading] =  useState({
        create: false,
        join: false
    });

    const [alerts, setAlerts] = useState({
        error: '',
        success: ''
    });

    const showAlert = (type, message) => {
        setAlerts({...alerts, [type]: message});
        setTimeout(() => {
            setAlerts({...alerts, [type]: ''});
        }, type ==='error' ? 5000 : 3000);
    };

    const handleInputChange = (field,value) => {
        setFormData({...formData, [field]: value});
    };

    const createSession = async () => {
        const {initiatorUsername} = formData;

        if (!initiatorUsername.trim()) {
            showAlert('error', 'Please enter your username');
            return;
        }

        setLoading({...loading, create: true});

        try {
            // Validate user existence
            const userValidation = await userAPI.validateUser(initiatorUsername);

            if (!userValidation.data.exists) {
                showAlert('error', 'User not found. Please contact administrator.');
                return;
            }

            // Create session
            const response = await sessionAPI.createSession(initiatorUsername);
            const sessionCode = response.data.sessionCode;

            showAlert('success', 'Session created successfully!');

            // Navigate to session page
            setTimeout(() => {
                navigate(`/session/${sessionCode}?user=${encodeURIComponent(initiatorUsername)}`);
            }, 1000);
            } catch (error) {
                showAlert('error', handleApiError(error));
            } finally {
                setLoading({...loading, create: false});
        }
    };

    const joinSession = async () => {
        const {joinSessionCode, joinUsername} = formData;

        if (!joinSessionCode.trim() || !joinUsername.trim()) {
            showAlert('error', 'Please enter both session code and username');
            return;
        }

        setLoading({...loading, join: true});

        try {
            // check session existence with uppercase
            await sessionAPI.getSession(joinSessionCode.toUpperCase());

            showAlert('success', 'Joined session successfully');

            // navigate to session page
            setTimeout(() => {
                navigate(`/session/${joinSessionCode.toUpperCase()}?user=${encodeURIComponent(joinUsername)}`);
            }, 1000);
        } catch (error) {
            showAlert('error', handleApiError(error));
        } finally {
            setLoading({...loading, join: false});
        }
    };

    const handleKeyPress = (event,action) => {
        if (event.key ==='Enter') {
            action();
        }
    };

    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-8">
                    <Card className="shadow">
                        <Card.Body>
                            <Card.Title className="text-center mb-4">
                                <h3>Get Started</h3>
                            </Card.Title>
                            
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

                            <div className="row">
                                <div className="col-md-6">
                                    <Card>
                                        <Card.Body>
                                            <Card.Title>Create New Session</Card.Title>
                                            <Form>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Your Username</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        placeholder="Enter your username"
                                                        value={formData.initiatorUsername}
                                                        onChange={(e) => handleInputChange('initiatorUsername', e.target.value)}
                                                        onKeyPress={(e) => handleKeyPress(e, createSession)}
                                                        disabled={loading.create}
                                                    />
                                                </Form.Group>
                                                <Button 
                                                    variant="primary" 
                                                    onClick={createSession}
                                                    disabled={loading.create}
                                                    className="w-100"
                                                >
                                                    {loading.create ? 'Creating...' : 'Create Session'}
                                                </Button>
                                            </Form>
                                        </Card.Body>
                                    </Card>
                                </div>
                                
                                <div className="col-md-6">
                                    <Card>
                                        <Card.Body>
                                            <Card.Title>Join Existing Session</Card.Title>
                                            <Form>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Session Code</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        placeholder="Enter session code"
                                                        value={formData.joinSessionCode}
                                                        onChange={(e) => handleInputChange('joinSessionCode', e.target.value)}
                                                        onKeyPress={(e) => handleKeyPress(e, joinSession)}
                                                        disabled={loading.join}
                                                    />
                                                </Form.Group>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Your Username</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        placeholder="Enter your username"
                                                        value={formData.joinUsername}
                                                        onChange={(e) => handleInputChange('joinUsername', e.target.value)}
                                                        onKeyPress={(e) => handleKeyPress(e, joinSession)}
                                                        disabled={loading.join}
                                                    />
                                                </Form.Group>
                                                <Button 
                                                    variant="success" 
                                                    onClick={joinSession}
                                                    disabled={loading.join}
                                                    className="w-100"
                                                >
                                                    {loading.join ? 'Joining...' : 'Join Session'}
                                                </Button>
                                            </Form>
                                        </Card.Body>
                                    </Card>
                                </div>
                            </div>
                        </Card.Body>
                    </Card>
                </div>
            </div>
        </div>
    );
};

export default LandingPage;
