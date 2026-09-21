import csv
import sys
import os
import argparse

def mock_translate(sentence_en, word_a, word_b):
    return {
        'sentence_bn': f"[Mock Bangla] {sentence_en}",
        'word_a_bn': f"[Mock Bangla for {word_a}]",
        'word_b_bn': f"[Mock Bangla for {word_b}]"
    }

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--mock', action='store_true', help="Use mock LLM generation")
    args = parser.parse_args()

    input_file = '03_sentences.csv'
    output_file = '05_review.csv'

    if not os.path.exists(input_file):
        print(f"Error: {input_file} not found.")
        sys.exit(1)

    rows = []
    with open(input_file, 'r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            rows.append(row)

    translated = []
    for row in rows:
        if args.mock:
            llm_result = mock_translate(row['sentence_en'], row['word_a'], row['word_b'])
        else:
            print("Real LLM not fully configured, falling back to mock.")
            llm_result = mock_translate(row['sentence_en'], row['word_a'], row['word_b'])
            
        row['sentence_bn'] = llm_result['sentence_bn']
        row['word_a_bn'] = llm_result['word_a_bn']
        row['word_b_bn'] = llm_result['word_b_bn']
        translated.append(row)

    with open(output_file, 'w', encoding='utf-8', newline='') as f:
        if not translated:
            return
        fieldnames = list(translated[0].keys())
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(translated)

    print(f"Successfully translated {len(translated)} rows into {output_file}.")

if __name__ == '__main__':
    main()
