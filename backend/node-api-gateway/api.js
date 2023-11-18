// const express = require('express')
// const request = require('request-promise-native')
// const port = 8080
// const app = express()
//
// // Resolve: GET /users/me
// app.get('/auth/user', async (req, res) => {
//     const uri = `https://auth-service/user/5`
//     const user = await request(uri)
//     res.json(user)
// })
//
const express = require('express');
const request = require('request-promise-native')
const port = 5000;

const app = express();

const cors = require('cors');
app.use(cors({
    origin: '*'
}));

app.listen(port, () => {
    console.log(`Server started on port ${port}`);
});

app.get('/auth/user', async (req, res) => {
    console.log("HEIII")
    const uri = `https://8001/user/5`
    const user = await request(uri)
    res.json(user)
})