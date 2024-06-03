import React, { useState, useEffect, useRef } from 'react';
import './Home.css';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const Home = () => {

    const [wsClient, setWsClient] = useState(null);
    const [connected, setConnnected] = useState(false);
    const [game, setGame] = useState({ gameId: '', playerName: '', playMode: false, playerRole: '', messages: [], gameState: 'PENDING' });
    const [messages, setMessages] = useState([]);

    useEffect(() => {
        const newClient = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/gameofthree/connect'),
            onConnect: () => {
                newClient.subscribe('/generic/game.event', (req) => {
                    handleGameEvents(JSON.parse(req.body));
                });
                newClient.subscribe('/user/specific/game.event', (req) => {
                    handleUserEvents(JSON.parse(req.body));
                });
                setConnnected(true);
            },
            onDisconnect: () => {
                setConnnected(false);
            },
            onWebSocketClose: () => {
                setConnnected(false);
            }
        });

        newClient.activate();
        setWsClient(newClient);

        return () => {
            newClient.deactivate();
        };
    }, []);

    const handleUserEvents = (event) => {
        let newGame = {};
        if (event.gameId !== null && event.gameId !== undefined) {
            newGame = { ...newGame, gameId: event.gameId }
        }
        if (event.playerRole !== null) {
            newGame = { ...newGame, playerRole: event.playerRole }
        }
        newGame = { ...newGame, gameState: event.gameState }
        setMessages(prevMessages => [...prevMessages, event.message])
        setGame((prevGame) => ({ ...prevGame, ...newGame }));
    };

    const handleGameEvents = (event) => {
        if (event.gameState === 'ERROR') {
            alert(event.message);
            setGame({ gameId: '', playerName: '', playMode: false, playerRole: '', messages: [], gameState: 'PENDING' });
            setMessages([])
        } else {
            setMessages(prevMessages => [...prevMessages, event.message]);
            setGame(prevGame => ({ ...prevGame, gameState: event.gameState }));
        }
    }

    const handleNameInput = (event) => {
        setGame(prevGame => ({ ...prevGame, playerName: event.target.value }));
    }

    const handlePlayMode = (event) => {
        setGame(prevGame => ({ ...prevGame, playMode: !game.playMode }))
    };

    const handlePlayerInfoOnSubmit = (event) => {
        event.preventDefault();
        if (game.playerName === null || game.playerName.trim().length === 0) {
            alert("Player name is mandatory!");
        } else {
            const playMode = game.playMode === true ? 'MANUAL' : 'AUTOMATIC';
            const request = { playerName: game.playerName, playMode: playMode };
            wsClient.publish({ destination: '/server/join', body: JSON.stringify(request) });
        }
    };

    const handleInputNumberOnChange = (event) => {
        setGame(prevGame => ({ ...prevGame, playerInputNumber: event.target.value }))
    }

    const handleInputNumberSubmit = () => {
        if (game.playerInputNumber === null || game.playerInputNumber.trim().length === 0) {
            alert("Mandatory to input a number to start the game!");
        } else {
            const request = { playerMove: game.playerInputNumber, gameId: game.gameId };
            wsClient.publish({ destination: '/server/play', body: JSON.stringify(request) });
        }
    }

    const handlePlayerNextMove = (number) => {
        const request = { playerMove: number, gameId: game.gameId };
        wsClient.publish({ destination: '/server/play', body: JSON.stringify(request) })
    }

    const isMyTurn = () => {
        return ('PLAYER1' === game.playerRole && 'PLAYER1_TURN' === game.gameState) || ('PLAYER2' === game.playerRole && 'PLAYER2_TURN' === game.gameState);
    };

    const resetState = () => {
        setGame({ gameId: '', playerName: '', playMode: false, playerRole: '', messages: [], gameState: 'PENDING' });
        setMessages([])
    };

    return (
        <div className='container'>
            {!connected &&
                <h4 style={{ color: 'red' }}>Waiting for server connection...</h4>
            }
            {game.playerName && <h4>Player: {game.playerName}</h4>}
            {game.gameState === 'COMPLETED' &&
                <button className='main-button' onClick={resetState}>Play Again</button>
            }
            {game.gameState === 'PENDING' &&
                <div className='name-input-div'>
                    <form onSubmit={handlePlayerInfoOnSubmit}>
                        <span>Enter your name: </span>
                        <input type='text' value={game.playerName} onChange={handleNameInput} className='name-input' />
                        <input type='checkbox' className='play-type' checked={game.playMode} onChange={handlePlayMode} /> <span>Manual Mode</span>
                        <button type='submit' className='submit-name'>Submit</button>
                    </form>
                </div>
            }
            {game.gameState === 'IN_PROGRESS' && game.playMode && game.playerRole === 'PLAYER1' &&
                <div className='number-input-div'>
                    <span><span style={{ fontWeight: 'bold' }}>{game.playerName}</span> since you're playing in Manual mode. Enter number to start: </span>
                    <input type='number' value={game.playerInputNumber} onChange={handleInputNumberOnChange} className='number-input' />
                    <button className='submit-name' onClick={handleInputNumberSubmit}>Submit</button>
                </div>
            }
            {isMyTurn() && game.playMode &&
                <div className='next-move'>
                    <span><span style={{ fontWeight: 'bold' }}>{game.playerName}</span> select your next move:</span>
                    <button className='next-move-btn' onClick={() => handlePlayerNextMove(-1)}>-1</button>
                    <button className='next-move-btn' onClick={() => handlePlayerNextMove(0)}>0</button>
                    <button className='next-move-btn' onClick={() => handlePlayerNextMove(+1)}>+1</button>
                </div>
            }
            <div className='game-log-div'>
                <h3>Game Log</h3>
                <table className='log-table'>
                    <tbody>
                        {messages.map((message, index) => (
                            <tr className='log-table-row' key={index}>
                                <td className='log-table-row' key={index + 1}><span>&#8226;</span> {message}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default Home;