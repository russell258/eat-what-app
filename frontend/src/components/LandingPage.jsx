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
        console.log('[LandingPage] Creating session');
        const {initiatorUsername} = formData;

        if (!initiatorUsername.trim()) {
            showAlert('error', 'Please enter your username');
            return;
        }

        setLoading({...loading, create: true});

        try {
            // Backend handles all validation now
            const response = await sessionAPI.createSession(initiatorUsername);
            const sessionCode = response.data.data.sessionCode;

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
        console.log('[LandingPage] Joining session');
        const {joinSessionCode, joinUsername} = formData;

        if (!joinSessionCode.trim() || !joinUsername.trim()) {
            showAlert('error', 'Please enter both session code and username');
            return;
        }

        setLoading({...loading, join: true});

        try {
            // Backend handles session validation now
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


    return (
        <div className="container mt-5">
            <div className="row justify-content-center">
                <div className="col-md-8">
                    <Card className="shadow">
                        <Card.Body>
                            <Card.Title className="text-center mb-4">
                                <h3>Eat what?</h3>
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
                                            <Form onSubmit={(e) => { e.preventDefault(); createSession(); }}>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Username:</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        placeholder="Enter your username"
                                                        value={formData.initiatorUsername}
                                                        onChange={(e) => handleInputChange('initiatorUsername', e.target.value)}
                                                        disabled={loading.create}
                                                        className="form-control-lg"
                                                    />
                                                </Form.Group>
                                                <Button 
                                                    variant="primary" 
                                                    type="submit"
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
                                            <Form onSubmit={(e) => { e.preventDefault(); joinSession(); }}>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Session Code:</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        placeholder="Enter session code"
                                                        value={formData.joinSessionCode}
                                                        onChange={(e) => handleInputChange('joinSessionCode', e.target.value)}
                                                        disabled={loading.join}
                                                        className="form-control-lg"
                                                    />
                                                </Form.Group>
                                                <Form.Group className="mb-3">
                                                    <Form.Label>Username:</Form.Label>
                                                    <Form.Control
                                                        type="text"
                                                        placeholder="Enter username"
                                                        value={formData.joinUsername}
                                                        onChange={(e) => handleInputChange('joinUsername', e.target.value)}
                                                        disabled={loading.join}
                                                        className="form-control-lg"
                                                    />
                                                </Form.Group>
                                                <Button 
                                                    variant="success" 
                                                    type="submit"
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
