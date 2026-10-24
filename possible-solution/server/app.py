# Create a base Flask server

import csv
import pickle
from pathlib import Path

from flask import Flask, jsonify, request

BASE_DIR = Path(__file__).resolve().parent
MODEL_PATH = BASE_DIR / 'model.pkl'
AIRPORTS_PATH = BASE_DIR / 'airports.csv'

app = Flask(__name__)


@app.after_request
def after_request(response):
    """Enable CORS for frontend requests."""
    response.headers.add('Access-Control-Allow-Origin', '*')
    response.headers.add('Access-Control-Allow-Headers', 'Content-Type,Authorization')
    return response


with MODEL_PATH.open('rb') as model_file:
    model = pickle.load(model_file)


@app.route('/predict', methods=['GET'])
def predict():
    """Return the delay probability and confidence for a given day and airport."""
    try:
        day_of_week = int(request.args.get('day_of_week', ''))
        airport_id = int(request.args.get('airport_id', ''))
    except (TypeError, ValueError):
        return jsonify({'error': 'day_of_week and airport_id must be integers'}), 400

    if not 1 <= day_of_week <= 7:
        return jsonify({'error': 'day_of_week must be between 1 and 7'}), 400

    if not airport_id:
        return jsonify({'error': 'airport_id is required'}), 400

    prediction = model.predict_proba([[day_of_week, airport_id]])[0]
    delay_probability = float(prediction[1])
    certainty = float(max(prediction))

    return jsonify({'certainty': certainty, 'delay': delay_probability})


@app.route('/airports', methods=['GET'])
def airports():
    """Return the list of airports sorted alphabetically by name."""
    with AIRPORTS_PATH.open('r', newline='') as airports_file:
        airport_rows = list(csv.DictReader(airports_file))

    airports = [
        {'id': int(row['OriginAirportID']), 'name': row['OriginAirportName']}
        for row in airport_rows
        if row.get('OriginAirportID') and row.get('OriginAirportName')
    ]
    airports = sorted(airports, key=lambda airport: airport['name'].casefold())
    return jsonify(airports)


if __name__ == '__main__':
    app.run(debug=True)